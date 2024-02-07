package de.ancash.sockets.async.plugin;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import de.ancash.ILibrary;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketFuture;

public abstract class AbstractSocketPlugin implements Listener {

	protected AsyncPacketClient chatClient;

	private final Logger logger;

	public AbstractSocketPlugin(Logger logger) {
		this.logger = logger;
		EventManager.registerEvents(this, this);
	}

	public synchronized void connect(String address, int port) {
		if (chatClient != null) {
			try {
				chatClient.onDisconnect(new IllegalStateException("Only one AsyncChatClient per plugin"));
			} catch (Exception ex) {
			}
			chatClient = null;
		}
		try {
			chatClient = ILibrary.ASYNC_CHAT_CLIENT_FACTORY.newInstance(address, port, 4 * 1024, 4 * 1024);
		} catch (IOException e) {
			logger.severe("Could not connect to " + address + ":" + port + ": " + e);
			;
		}
	}

	public PacketFuture sendPacket(Packet packet) {
		return sendPacket(packet, null);
	}

	public PacketFuture sendPacket(Packet packet, UUID uuid) {
		try {
			chatClient.write(packet);
		} catch (InterruptedException e) {
			return null;
		}
		return new PacketFuture(packet, uuid);
	}

	@EventHandler
	public void onPacket(ClientPacketReceiveEvent event) {
		if (event.getReceiver() == null || chatClient == null || event.getReceiver().equals(chatClient))
			this.onPacketReceive(event.getPacket());
	}

	@EventHandler
	public void onClientDisconnect(ClientDisconnectEvent event) {
		if (event.getClient() == null || chatClient == null || event.getClient().equals(chatClient))
			this.onClientDisconnect(event.getClient());
	}

	@EventHandler
	public void onClientConnect(ClientConnectEvent event) {
		if (event.getClient() == null || chatClient == null || event.getClient().equals(chatClient))
			this.onClientConnect(event.getClient());
	}

	public abstract void onClientDisconnect(AbstractAsyncClient client);

	public abstract void onClientConnect(AbstractAsyncClient client);

	public abstract void onPacketReceive(Packet packet);
}