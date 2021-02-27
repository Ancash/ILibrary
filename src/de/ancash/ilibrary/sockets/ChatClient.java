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
	private final String plugin;
	
	public final void send(Packet str) {
		try {
			streamOut.write(SerializationUtils.serialize(str));
			streamOut.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public ChatClient(String serverName, int serverPort, String plugin){
		this.plugin = plugin;
		System.out.println("Establishing connection. Please wait ..." + " (" + plugin + ")");
		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket + " (" + plugin + ")");
			start();
		} catch(UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage() + " (" + plugin + ")"); 
		} catch(IOException ioe){
			System.out.println("Unexpected exception: " + ioe.getMessage() + " (" + plugin + ")"); 
		}
	}

	private final void start() throws IOException {
		streamOut = new DataOutputStream(socket.getOutputStream());
		if (client == null) {  
			client = new ChatClientThread(this, socket);
		}
		//runnable.runTaskTimer(plugin, 0, 0);
	}
	
	public final boolean isActive() {
		return client != null && streamOut != null;
	}
	
	public abstract void onRequest(Request req);
	
	public abstract void onAnswer(Answer ans);
	
	public abstract void onInfo(InfoPacket packet);
	
	public final void stop() {  
		try{
			if (streamOut != null)  streamOut.close();
			if (socket    != null)  socket.close();
		} catch(IOException ioe){
			System.out.println("Error closing ..." + " (" + plugin + ")"); 
		}
		client.close();  
		client.stop();
		client = null;
	}
}

