package de.ancash.ilibrary.sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import de.ancash.ilibrary.misc.SerializationUtils;

public abstract class ChatClient{
	
	private Socket socket              = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client    = null;
	private final String name;
	
	public final void send(Packet str) {
		//System.out.println("" + SerializationUtils.serialize(str, streamOut));
		try {
			streamOut.write(SerializationUtils.serializeToBytes(str));
			streamOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ChatClient(String serverName, int serverPort, String name){
		this.name = name;
		System.out.println("Establishing connection. Please wait ..." + " (" + name + ")");
		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket + " (" + name + ")");
			start();
		} catch(UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage() + " (" + name + ")"); 
		} catch(IOException ioe){
			System.out.println("Unexpected exception: " + ioe.getMessage() + " (" + name + ")"); 
		}
	}

	private final void start() throws IOException {
		streamOut = new DataOutputStream(socket.getOutputStream());
		if (client == null) {  
			client = new ChatClientThread(this, socket);
		}
	}
	
	public final boolean isActive() {
		return client != null && streamOut != null;
	}
			
	public abstract void onPacket(Packet packet);
	
	public final void stop() {  
		try{
			if (streamOut != null)  streamOut.close();
			if (socket    != null)  socket.close();
		} catch(IOException ioe){
			System.out.println("Error closing ..." + " (" + name + ")"); 
		}
		client.close();  
		client.stop();
		client = null;
	}
}

