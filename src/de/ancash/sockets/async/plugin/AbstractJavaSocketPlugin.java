package de.ancash.sockets.async.plugin;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.ILibrary;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.HandlerList;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.io.ITCPClient;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketFuture;

public abstract class AbstractJavaSocketPlugin extends JavaPlugin implements Listener {

	static AtomicInteger cnt = new AtomicInteger();
	
	protected AsyncPacketClient chatClient;
	private String ref;
	
	public synchronized void connect(String address, int port) {
		
		if (chatClient != null) {
			try {
				chatClient.disconnect(new IllegalStateException("Only one AsyncChatClient per plugin"));
			} catch (Exception ex) {
			}
			chatClient = null;
		}
		try {
			ref = "SocketPlugin: " + cnt.getAndIncrement();
			EventManager.registerEvents(this, ref);
			chatClient = ILibrary.ASYNC_CHAT_CLIENT_FACTORY.newInstance(address, port, 16 * 1024, 16 * 1024);
		} catch (IOException e) {
			getLogger().severe("Could not connect to " + address + ":" + port + ": " + e);
		}
	}

	public PacketFuture sendPacket(Packet packet) {
		return sendPacket(packet, null);
	}

	public PacketFuture sendPacket(Packet packet, UUID uuid) {
		chatClient.write(packet);
		return new PacketFuture(packet, uuid);
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		if (event.getReceiver().equals(chatClient))
			this.onPacketReceive(event.getPacket());
	}

	@EventHandler
	public void onClientDisconnect(ClientDisconnectEvent event) {
		if (event.getClient().equals(chatClient)) {
			HandlerList.unregisterAll(ref);
			this.onClientDisconnect(event.getClient());
		}
	}

	@EventHandler
	public void onClientConnect(ClientConnectEvent event) {
		if (event.getClient().equals(chatClient))
			this.onClientConnect(event.getClient());
	}

	public abstract void onClientDisconnect(ITCPClient client);

	public abstract void onClientConnect(ITCPClient client);

	public abstract void onPacketReceive(Packet packet);
}