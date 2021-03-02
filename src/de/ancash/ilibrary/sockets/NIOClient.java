package de.ancash.ilibrary.sockets;

public abstract class NIOClient extends ChatClient{

	public NIOClient(String serverName, int serverPort, String plugin) {
		super(serverName, serverPort, plugin);
	}

}
