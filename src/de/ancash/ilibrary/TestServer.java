package de.ancash.ilibrary;

import java.io.IOException;

import de.ancash.ilibrary.sockets.NIOServer;

public class TestServer extends NIOServer{

	public TestServer(String host, int port) throws IOException {
		super(host, port);
	}

}
