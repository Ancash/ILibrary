package de.ancash.sockets.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executors;

import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.AbstractAsyncClientFactory;
import de.ancash.sockets.async.server.AbstractAsyncServer;

public class AsyncChatClientFactory extends AbstractAsyncClientFactory{

	@Override
	public AbstractAsyncClient newInstance(AbstractAsyncServer asyncServer, AsynchronousSocketChannel socket, int queueSize, int readBufSize, int writeBufSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncChatClient newInstance(String address, int port, int queueSize, int readBufSize, int writeBufSize, int threads) throws IOException {
		AsynchronousChannelGroup asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(3));
		AsynchronousSocketChannel asyncSocket = AsynchronousSocketChannel.open(asyncChannelGroup);
		AsyncChatClient client = new AsyncChatClient(asyncSocket, asyncChannelGroup, queueSize, readBufSize, writeBufSize, threads);
		asyncSocket.connect(new InetSocketAddress(address, port), client, client.getAsyncConnectHandlerFactory().newInstance(client));
		return client;
	}
}