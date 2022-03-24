package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncReadHandler;

public class AsyncChatClientReadHandler extends AbstractAsyncReadHandler{

	public AsyncChatClientReadHandler(AbstractAsyncClient asyncClient, int readBufSize) {
		super(asyncClient, readBufSize);
	}
}