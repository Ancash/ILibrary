package de.ancash.ilibrary.sockets;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import de.ancash.ilibrary.misc.SerializationUtils;

class ChatClientThread{
	private Socket           socket   = null;
	private ChatClient       client   = null;
	private DataInputStream  streamIn = null;
	private Thread thread;
	
	ChatClientThread(ChatClient _client, Socket _socket){
		client   = _client;
		socket   = _socket;
		open();  
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(thread != null) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Packet packet = null;
					try {
						if(streamIn.available() <= 0) continue;
						byte[] bytes = new byte[streamIn.available()];
						streamIn.read(bytes);
						packet = (Packet) SerializationUtils.deserializeFromBytes(bytes);
						client.onPacket(packet);
					} catch (Exception e) {
						System.out.println("Error while reading input stream! Stopping...");
						client.stop();
						stop();
						return;
					}
				}
			}
		});
		new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				thread.start();
			}
		}.run();
	}
	public void open(){
		try{
			streamIn  = new DataInputStream(socket.getInputStream());
		} catch(IOException ioe){
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}
	public void close(){
		try{
			if (streamIn != null) streamIn.close();
		}catch(IOException ioe){
			System.out.println("Error closing input stream: " + ioe);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		close();
		thread.stop();
		thread = null;
	}
}
