from enum import Enum, auto

class ACTION(Enum):
    UPDATE = auto()
    QUEUE_SONG = auto()
    PLAY_SONG = auto()
    SKIP_SONG = auto()
    PAUSE = auto()
    RESUME = auto()
    CHANGE_VOLUME = auto()
    ADD_SONG_TO_QUEUE = auto()
    TOGGLE_REPLAY = auto()


class EVENT:
    action = None
    data = None
    def __init__(self, action):
        self.action = action