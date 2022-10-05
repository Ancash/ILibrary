package de.ancash.sockets.async.plugin;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.ILibrary;
import de.ancash.libs.org.bukkit.event.EventHandler;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.packet.PacketFuture;
import de.ancash.sockets.async.client.AbstractAsyncClient;
import de.ancash.sockets.events.ClientConnectEvent;
import de.ancash.sockets.events.ClientDisconnectEvent;
import de.ancash.sockets.events.ClientPacketReceiveEvent;
import de.ancash.sockets.packet.Packet;

public abstract class AbstractJavaSocketPlugin extends JavaPlugin implements Listener {

	protected AsyncPacketClient chatClient;

	public AbstractJavaSocketPlugin() {
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
			chatClient = ILibrary.ASYNC_CHAT_CLIENT_FACTORY.newInstance(address, port, 10_000, 256 * 1024, 256 * 1024,
					2);
		} catch (IOException e) {
			getLogger().severe("Could not connect to " + address + ":" + port + ": " + e);
			;
		}
	}

	public PacketFuture sendPacket(Packet packet) {
		return sendPacket(packet, null);
	}

	public PacketFuture sendPacket(Packet packet, UUID uuid) {
		if (chatClient != null)
			chatClient.write(packet);
		else
			ILibrary.getInstance().send(packet);
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