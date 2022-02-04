package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncConnectHandler;
import de.ancash.sockets.async.client.AbstractAsyncConnectHandlerFactory;

public class AsyncChatClientConnectHandlerFactory extends AbstractAsyncConnectHandlerFactory{

	@Override
	public AbstractAsyncConnectHandler newInstance(AbstractAsyncClient client) {
		return new AsyncChatClientConnectHandler(client);
	}

}
