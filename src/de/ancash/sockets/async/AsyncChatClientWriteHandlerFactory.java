package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncWriteHandler;
import de.ancash.sockets.async.client.AbstractAsyncWriteHandlerFactory;

public class AsyncChatClientWriteHandlerFactory extends AbstractAsyncWriteHandlerFactory{

	@Override
	public AbstractAsyncWriteHandler newInstance(AbstractAsyncClient asyncClient)  {
		return new AsyncChatClientWriteHandler(asyncClient);
	}
}