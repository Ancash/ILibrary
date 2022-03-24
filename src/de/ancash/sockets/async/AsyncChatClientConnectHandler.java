package de.ancash.sockets.async;

import java.io.IOException;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncConnectHandler;

public class AsyncChatClientConnectHandler extends AbstractAsyncConnectHandler{

	public AsyncChatClientConnectHandler(AbstractAsyncClient client) {
		super(client);
	}
	
	@Override
	public void completed(Void arg0, AbstractAsyncClient arg1) {
		try {
			System.out.println("Connected to " + arg1.getAsyncSocketChannel().getRemoteAddress() + " from " + arg1.getAsyncSocketChannel().getLocalAddress() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.completed(arg0, arg1);
	}
	
	@Override
	public void failed(Throwable arg0, AbstractAsyncClient arg1) {
		System.err.println("Could not connect: " + arg0);
		super.failed(arg0, arg1);
	}
}