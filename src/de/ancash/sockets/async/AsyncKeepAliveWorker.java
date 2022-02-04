package de.ancash.sockets.async;

import java.io.IOException;

import de.ancash.sockets.packet.Packet;

public class AsyncKeepAliveWorker implements Runnable{

	private final AsyncChatClient client;
	private final Packet keepAlivePacket = new Packet(Packet.KEEP_ALIVE_HEADER);
	
	public AsyncKeepAliveWorker(AsyncChatClient client) {
		this.client = client;
		keepAlivePacket.isClientTarget(false);
		keepAlivePacket.setAwaitResponse(true);
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("ClientKeepAliveWorker");
		while(!client.isConnected()) {}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			System.err.println(Thread.currentThread().getName() + "Stopping: " + e1);
			return;
		}
		
		while(client.isConnected()) {
			try {
				long l = System.currentTimeMillis();
				client.write(keepAlivePacket);
				keepAlivePacket.awaitResponse(5000).get();
				keepAlivePacket.resetResponse();
				long sleep = (l + 5000) - System.currentTimeMillis();
				Thread.sleep(sleep);
			} catch (InterruptedException | IOException e) {
				System.out.println(Thread.currentThread().getName() + " - Timed Out! Stopping...");
				return;
			}
		}
	}
	
}
