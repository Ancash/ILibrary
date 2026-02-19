package de.ancash.sockets.async.plugin;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.sockets.async.impl.packet.client.netty.NettyPacketClient;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.packet.PacketFuture;

public abstract class AbstractJavaNettyPlugin extends JavaPlugin {

	static AtomicInteger cnt = new AtomicInteger();

	protected NettyPacketClient client;

	public synchronized boolean connect(String address, int port) {

		if (client != null) {
			client.disconnect(new IllegalStateException());
			client = null;
		}
		try {
			client = new NettyPacketClient(address, port, this::onPacketReceive, this::onClientConnect, this::onClientDisconnect);
			client.connect();
			return client.isConnected();
		} catch (InterruptedException e) {
			getLogger().severe("Could not connect to " + address + ":" + port + ": " + e);
			return false;
		}
	}

	public PacketFuture sendPacket(Packet packet) {
		return sendPacket(packet, null);
	}

	public PacketFuture sendPacket(Packet packet, UUID uuid) {
		client.write(packet);
		return new PacketFuture(packet, uuid);
	}

	public abstract void onClientDisconnect();

	public abstract void onClientConnect();

	public abstract void onPacketReceive(Packet packet);
}