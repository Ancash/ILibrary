package de.ancash.sockets.async;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.client.IPacketSender;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketCallback;
import de.ancash.sockets.packet.PacketCombiner;
import de.ancash.sockets.packet.UnfinishedPacket;

public class AsyncChatClient extends AbstractAsyncClient implements Listener, IPacketSender{

	private final Map<Long, PacketCallback> packetCallbacks = new HashMap<>();
	private final Map<Long, Packet> awaitResponses = new HashMap<>();
	private final Object futureSync = new Object();
	private final PacketCombiner packetCombiner = new PacketCombiner();
	private ExecutorService clientThreadPool = Executors.newCachedThreadPool();
	private ArrayBlockingQueue<Duplet<AsyncChatClient, UnfinishedPacket>> unfinishedPacketsQueue = new ArrayBlockingQueue<>(1000);
	private final int worker;
	private AsynchronousChannelGroup asyncChannelGroup;
	
	public AsyncChatClient(AsynchronousSocketChannel asyncSocket, AsynchronousChannelGroup asyncChannelGroup, int queueSize, int readBufSize, int writeBufSize, int worker) throws IOException {
		super(asyncSocket, queueSize, readBufSize, writeBufSize);
		this.asyncChannelGroup = asyncChannelGroup;
		setAsyncClientFactory(new AsyncChatClientFactory());
		setAsyncReadHandlerFactory(new AsyncChatClientReadHandlerFactory());
		setAsyncWriteHandlerFactory(new AsyncChatClientWriteHandlerFactory());
		setAsyncConnectHandlerFactory(new AsyncChatClientConnectHandlerFactory());
		setHandlers();
		this.worker = worker;
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		synchronized (futureSync) {
			Packet packet = event.getPacket();
			Optional.ofNullable(packetCallbacks.remove(packet.getTimeStamp())).ifPresent(sc -> sc.call(packet.getSerializable()));
			Optional.ofNullable(awaitResponses.remove(packet.getTimeStamp())).ifPresent(await -> await.awake(packet));
		}
	}
	
	public final void write(Packet packet) {
		
		packet.addTimeStamp();
		synchronized (futureSync) {
			if(packet.hasPacketCallback()) packetCallbacks.put(packet.getTimeStamp(), packet.getPacketCallback());
			if(packet.isAwaitingRespose()) awaitResponses.put(packet.getTimeStamp(), packet);
		}

		write(packet.toBytes());
	}
	
	@Override
	public void onBytesReceive(byte[] b) {
		for(UnfinishedPacket packet : packetCombiner.put(b))
			try {
				unfinishedPacketsQueue.put(Tuple.of(this, packet));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void onConnect() {
		EventManager.registerEvents(this, this);
		for(int i = 0; i<worker; i++) 
			clientThreadPool.submit(new ClientPacketWorker(unfinishedPacketsQueue, i + 1));
		EventManager.callEvent(new ClientConnectEvent(this));
	}
	
	@Override
	public synchronized void onDisconnect(Throwable th) {
		if(asyncChannelGroup == null) return;
		try {
			asyncChannelGroup.shutdownNow();
			getAsyncSocketChannel().close();
		} catch(IOException e) {
			
		}
		clientThreadPool.shutdownNow();
		clientThreadPool = null;
		EventManager.callEvent(new ClientDisconnectEvent(this));
		asyncChannelGroup = null;
	}

	@Override
	public boolean isConnectionValid() {
		return isConnected();
	}
}