package de.ancash.ilibrary.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

abstract class ChatServer implements Runnable{
	private ChatServerThread clients[];
	private ServerSocket server = null;
	private int clientCount = 0;
	private Thread thread;
	private final String owner;	
	
	public ChatServer(int port, int clientCount, String owner){
		this.owner = owner;
		clients = new ChatServerThread[clientCount];
		try{
			System.out.println("Binding to port " + port + ", please wait  ..." + " (" + owner + ")");
			server = new ServerSocket(port);  
			System.out.println("Server started: " + server + " (" + owner + ")");
			start();
		}catch(IOException ioe){
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage() + " (" + owner + ")");
		}
	}
	
	@Override
	public final void run(){
		while (thread != null){
			try{
				System.out.println("Waiting for a client ..." + " (" + owner + ")"); 
				addThread(server.accept()); 
				
			} catch(IOException ioe){
				System.out.println("Server accept error: " + ioe + " (" + owner + ")");
				try {
					stop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
		   	}
	   	}
	}
   	
	public final void start(){
	   if (thread == null){
		   thread = new Thread(this); 
		   thread.start();
	   }
   	}
	
   	@SuppressWarnings("deprecation")
   	public final void stop() throws IOException{
   		if (thread != null){
   			System.out.println("Stopping Chat Server..." + " (" + owner + ")");
   			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   			thread.stop(); 
   			removeAllClients();
   			thread = null;
   		}
   	}
   
   	private final int findClient(int ID){
   		for (int i = 0; i < clientCount; i++)
   			if (clients[i].getID() == ID)
   				return i;
   		return -1;
   	}
   	
   	public final void sendAll(Packet msg) throws IOException {
   		//if(clientCount == 1) System.out.println("§cThere are no clients to send the packets to!");
   		for (int i = 0; i < clientCount; i++) 
   			clients[i].send(msg);
   	}
   	
   	public final void sendAllExceptSender(int sender, Packet msg) throws IOException {
   		//if(clientCount == 1) System.out.println("§cThere are no clients to send the packets to!");
   		for (int i = 0; i < clientCount; i++) 
   			if(clients[i].getID() != sender) clients[i].send(msg);
   	}
   	
   	public final void send(Packet msg, int id) throws IOException {
   		//if(clientCount == 1) System.out.println("§cThere are no clients to send the packets to!"); 
   		for (int i = 0; i < clientCount; i++) 
   			if(id != clients[i].getID()) clients[i].send(msg);
   	}
   	
   	public final void removeAllClients() throws IOException {
   		for(ChatServerThread cst : clients) {
   			if(cst == null) continue;
   			removeClient(cst.getID());
   		}
   	}
   	
	public final void removeClient(int ID) throws IOException{
   		int pos = findClient(ID);
   		if (pos >= 0){
   			ChatServerThread toTerminate = clients[pos];
   			System.out.println("Removing client thread " + ID + " at " + pos);
   			if (pos < clientCount-1)
   				for (int i = pos+1; i < clientCount; i++)
   					clients[i-1] = clients[i];
   			clientCount--;
   			try{
   				toTerminate.close(); 
   			}catch(IOException ioe){
   				System.out.println("Error closing thread: " + ioe); 
   			}
   			toTerminate.close();
   			toTerminate.stop();
   		}
   	}
	
   	private final void addThread(Socket socket){
   		if (clientCount < clients.length){
   			System.out.println("Client accepted: " + socket + " (" + owner + ")");
   			clients[clientCount] = new ChatServerThread(this, socket, owner);
   			try{
   				clients[clientCount].open(); 
   				clients[clientCount].start();  
   				clientCount++; 
   			}catch(IOException ioe){
   				System.out.println("Error opening thread: " + ioe + " (" + owner + ")");
   			} 
   		}
   		else
   			System.out.println("§cClient refused: maximum " + clients.length + " reached." + " (" + owner + ")");
   	}
   	
   	public abstract void onPacket(Packet packet);
}
