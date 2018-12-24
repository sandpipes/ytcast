import sys, socket, select, os, time, signal, vlc, queue, pafy, tempfile, requests, os
from server_actions import ACTION, EVENT
from threading import Thread

RUNNING = True
HOST = '192.168.2.97'
PORT = 8888

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

SOCKET_LIST = []
SOCKET_LIST.append(server_socket)

PENDING_ACTIONS = []

vlcInstance = vlc.Instance()
tempPATH = os.path.join(tempfile.gettempdir(), '')

def isRunning() -> bool:
    return RUNNING

def KillThread():
    global RUNNING
    RUNNING = False

def server_loop():
    server_socket.bind((HOST, PORT))
    server_socket.listen(10)
    RECV_BUFFER = 4096

    try:
        while isRunning():
            read, write, error = select.select(SOCKET_LIST,[],[])
            for sock in read:
                if sock == server_socket:
                    sockfd, addr = server_socket.accept()
                    SOCKET_LIST.append(sockfd)
                    print("User joined server.")
                else:
                    try:
                        rev = sock.recv(RECV_BUFFER)
                        if rev:
                            print(rev)
                            data = rev.decode()
                            print(data)
                            if data[:2] == 'UP':
                                PENDING_ACTIONS.append(EVENT(ACTION.UPDATE))
                            elif data[:2] == 'VO':
                                event = EVENT(ACTION.CHANGE_VOLUME)
                                event.data = data[2:]
                                PENDING_ACTIONS.append(event)
                            elif data[:2] == 'QS':
                                qs = EVENT(ACTION.QUEUE_SONG)
                                qs.data = data[2:]
                                PENDING_ACTIONS.append(qs)
                            elif data[:2] == 'PA':
                                PENDING_ACTIONS.append(EVENT(ACTION.PAUSE))
                            elif data[:2] == 'RE':
                                PENDING_ACTIONS.append(EVENT(ACTION.RESUME))
                            elif data[:2] == 'SK':
                                PENDING_ACTIONS.append(EVENT(ACTION.SKIP_SONG))
                            elif data[:2] == 'KL':
                                KillThread()
                            elif data[:2] == 'TR':
                                PENDING_ACTIONS.append(EVENT(ACTION.TOGGLE_REPLAY))
                        else:
                            if sock in SOCKET_LIST:
                                SOCKET_LIST.remove(sock)
                    except Exception as e:
                        if sock in SOCKET_LIST:
                            SOCKET_LIST.remove(sock)
                        print(e)
    except KeyboardInterrupt:
        KillThread()

    server_socket.close()

def finished_download(path, name, id):
    song = vlcInstance.media_new(path)

    event = EVENT(ACTION.ADD_SONG_TO_QUEUE)
    event.data = song
    PENDING_ACTIONS.append(event)
    time.sleep(1)

def download(q):
    while isRunning():
        item = q.get()
        if item is None:
            break
        print('Downloading song.')
        v = pafy.new(item)
        i = v.getbestaudio()
        r = requests.get(i.url, headers={'Range': 'bytes=0-'})
        p = tempPATH + v.videoid
        with open(p, 'wb') as f:
            f.write(r.content)

        finished_download(p, v.title, v.videoid)

        q.task_done()

def update_loop():
    player = vlcInstance.media_player_new()
    player.audio_set_volume(50)
    song_queue = []
    current_song = None
    workQ = queue.Queue()
    dthread = Thread(target=download, args=[workQ])
    dthread.start()

    repeat = False

    while isRunning():
        if player.get_state() == vlc.State.Ended and not repeat:
            if len(song_queue) > 0:
                print('Playing next song in queue.')
                player.set_media(song_queue[0])
                player.play()
                current_song = song_queue[0]
                del song_queue[0]
        elif repeat:
            player.play()
        else:
            pass
            #print(player.get_state())

        actions = list(PENDING_ACTIONS)
        for i in actions:
            if i.action == ACTION.UPDATE:
                print('Update event called.')

            elif i.action == ACTION.CHANGE_VOLUME:
                try:
                    if player.audio_set_volume(int(i.data)) == -1:
                        print('Volume out of range. (' + i.data + ')')
                except ValueError:
                    print(i.data + ' is not an integer.')
            
            elif i.action == ACTION.QUEUE_SONG:
                workQ.put(i.data)
                print('Placed song in worker queue.')

            elif i.action == ACTION.ADD_SONG_TO_QUEUE:
                print('Adding song to queue.')
                song_queue.append(i.data)
                if not current_song:
                    player.set_media(song_queue[0])
                    player.play()
                    current_song = song_queue[0]
                    del song_queue[0]
            
            elif i.action == ACTION.PAUSE:
                player.set_pause(1)
                print('Pause')

            elif i.action == ACTION.RESUME:
                player.set_pause(0)
                print('Resume')

            elif i.action == ACTION.PLAY_SONG:
                if len(song_queue) > 0:
                    player.play()
                else:
                    print('No song in queue.')

            elif i.action == ACTION.SKIP_SONG:
                if len(song_queue) > 0:
                    player.set_media(song_queue[0])
                    player.play()
                    current_song = song_queue[0]
                    del song_queue[0]
                else:
                    print('Can\'t skip, no more songs in queue.')
            elif i.action == ACTION.TOGGLE_REPLAY:
                repeat = not repeat
            else:
                print('Unknown action pending.')
            PENDING_ACTIONS.remove(i)
        time.sleep(0.5)

    workQ.put(None)
    dthread.join()

if __name__ == '__main__':
    update_thread = Thread(target=update_loop, args=[])
    update_thread.start()
    server_loop()
    update_thread.join()