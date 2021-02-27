package de.ancash.ilibrary;

import java.io.IOException;

import de.ancash.ilibrary.sockets.Answer;
import de.ancash.ilibrary.sockets.ChatServer;
import de.ancash.ilibrary.sockets.InfoPacket;
import de.ancash.ilibrary.sockets.Request;
import de.ancash.ilibrary.sockets.TargetType;

public class TestServer extends ChatServer{

	public TestServer(int port, int clientCount, String owner) {
		super(port, clientCount, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onRequest(int id, Request packet) {
		System.out.println(packet.getRequest());
		try {
			sendAll(new Answer("ALibrary", "Pong", TargetType.CLIENT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onAnswer(int id, Answer packet) {
		System.out.println(packet.getAnswer());
	}

	@Override
	public void onInfo(int id, InfoPacket packet) {
		// TODO Auto-generated method stub
		
	}

	
}
