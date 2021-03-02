package de.ancash.ilibrary;

import de.ancash.ilibrary.sockets.NIOClient;
import de.ancash.ilibrary.sockets.Packet;

public class TestClient extends NIOClient{

	public TestClient(String serverName, int serverPort, String plugin) {
		super(serverName, serverPort, plugin);
	}

	@Override
	public void onPacket(Packet packet) {
		
	}
}
