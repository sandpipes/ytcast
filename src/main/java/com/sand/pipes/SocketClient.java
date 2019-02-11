package com.sand.pipes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {
	private Socket socket; 
    private BufferedInputStream input; 
    private BufferedOutputStream out; 
    private boolean connected = false;
  
    private long last = System.nanoTime();
    
    public SocketClient() { 
  
    	/*
        String line = ""; 
        BufferedReader d = new BufferedReader(new InputStreamReader(input));

        while (!line.equals("Over")) 
        { 
            try
            { 
                line = d.readLine(); 
                out.writeUTF(line); 
            } 
            catch(IOException i) 
            { 
                System.out.println(i); 
            } 
        } 
        */
    } 
    
    public int close() {
    	if(!connected) return -2;
    	
        try{
            input.close(); 
            out.close(); 
            socket.close(); 
            connected = false;
        } catch(IOException i) {
            System.out.println(i); 
            return -1;
        }
        
        return 0;
    }
  
    public int connect(String address, int port) {
        try{
        	if(address == null)
        		return -3;
        	
            socket = new Socket(address, port); 
            System.out.println("Connected"); 
            connected = true;
            input = new BufferedInputStream(System.in); 
            out = new BufferedOutputStream(socket.getOutputStream()); 
            
        } catch(UnknownHostException u) { 
            System.out.println(u);
            return -1;
        } catch(IOException i) { 
            System.out.println(i);
            return -2;
        } 
        
    	return 0;
    }
    
    public void send(String s) {
    	if(System.nanoTime() - last > 10000000)
    		last = System.nanoTime();
    	else
    		return;
    	
        try{ 
        	out.write(s.getBytes());
        	out.flush();
        } catch(IOException i) { 
            System.out.println(i); 
        } 
    }
}
