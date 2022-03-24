package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncReadHandlerFactory;

public class AsyncChatClientReadHandlerFactory extends AbstractAsyncReadHandlerFactory{

	@Override
	public AsyncChatClientReadHandler newInstance(AbstractAsyncClient socket, int readBufSize) {
		return new AsyncChatClientReadHandler(socket, readBufSize);
	}
}