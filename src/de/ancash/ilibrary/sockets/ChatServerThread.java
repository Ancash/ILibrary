package de.ancash.ilibrary.sockets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import de.ancash.ilibrary.misc.SerializationUtils;

class ChatServerThread {
	private ChatServer       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  streamIn  =  null;
	private DataOutputStream streamOut = null;
	private Thread thread;
	private final String owner;
	
	public ChatServerThread(ChatServer _server, Socket _socket, String owner){
		this.owner = owner;
		server = _server;
		socket = _socket;
		ID     = socket.getPort();
	}
	
	public void send(Packet msg) throws IOException{
		
		try{
			streamOut.write(SerializationUtils.serialize(msg));
			streamOut.flush();
		}catch(IOException ioe){
			System.out.println(getID() + " ERROR sending: " + ioe.getMessage() + " (" + owner + ")");
			server.removeClient(getID());
		}
	}
	
	void start() {
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(thread != null) {
					Packet packet = null;
					try {
						packet = SerializationUtils.deserialize(streamIn);
					} catch (Exception e1) {						
						try {
							server.removeClient(ID);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;
					}
					
					if(packet.getTarget() == TargetType.SERVER) {
						if(packet instanceof Request) {
				   			server.onRequest(ID, (Request) packet); 
				   		} else if (packet instanceof Answer) {
				   			server.onAnswer(ID, (Answer) packet);
				   		} else if (packet instanceof InfoPacket) {
				   			server.onInfo(ID, (InfoPacket) packet);
				   		}
					} else if (packet.getTarget() == TargetType.CLIENT){
						try {
							server.sendAllExceptSender(ID, packet);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		});
		thread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			thread.stop();
			thread = null;
		}
	}
	
	public int getID(){
		return ID;
	}
	
	void open() throws IOException{
		streamIn = new DataInputStream(new 
				BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new
				BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void close() throws IOException{
		if (socket != null)    socket.close();
		if (streamIn != null)  streamIn.close();
		if (streamOut != null) streamOut.close();
	}
}
