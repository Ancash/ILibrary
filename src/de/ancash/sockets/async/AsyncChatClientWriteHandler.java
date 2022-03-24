package de.ancash.sockets.async;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncWriteHandler;

public class AsyncChatClientWriteHandler extends AbstractAsyncWriteHandler{

	public AsyncChatClientWriteHandler(AbstractAsyncClient asyncClient) {
		super(asyncClient);
	}
}