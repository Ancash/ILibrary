package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncConnectHandler;

public class AsyncChatClientConnectHandler extends AbstractAsyncConnectHandler{

	public AsyncChatClientConnectHandler(AbstractAsyncClient client) {
		super(client);
	}
	
	@Override
	public void completed(Void arg0, AbstractAsyncClient arg1) {
		System.out.println("Connected to " + arg1.getRemoteAddress());
		super.completed(arg0, arg1);
	}
	
	@Override
	public void failed(Throwable arg0, AbstractAsyncClient arg1) {
		System.err.println("Could not connect: " + arg0);
		super.failed(arg0, arg1);
	}
}