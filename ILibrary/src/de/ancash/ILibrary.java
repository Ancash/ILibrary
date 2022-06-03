package de.ancash;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;

import de.ancash.libs.org.apache.commons.io.FileUtils;
import de.ancash.libs.org.bukkit.event.Event;
import de.ancash.libs.org.bukkit.event.EventExecutor;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.HandlerList;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.libs.org.bukkit.event.Order;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;

import de.ancash.minecraft.crafting.ICraftingManager;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.updatechecker.UpdateCheckSource;
import de.ancash.minecraft.updatechecker.UpdateChecker;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClient;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClientFactory;
import de.ancash.sockets.packet.Packet;

public class ILibrary extends JavaPlugin{

	public static final AsyncPacketClientFactory ASYNC_CHAT_CLIENT_FACTORY = new AsyncPacketClientFactory();
	
	private AsyncPacketClient asyncClient;
	private int port;
	private static ILibrary plugin;
	private YamlFile f;

	public ILibrary() {
		plugin = this;
		f = new YamlFile(new File("plugins/ILibrary/config.yml"));
		try {
			//a
			if(!f.exists()) 
				FileUtils.copyInputStreamToFile(getResource("config.yml"), new File(f.getFilePath()));
			
			f.load();
			port = f.getInt("port");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnable() {		
		ICraftingManager.getSingleton().init(this);
		if(f.getBoolean("chat-client")) {
			new BukkitRunnable() {
					
				@Override
				public void run() {
					try {
						asyncClient = ASYNC_CHAT_CLIENT_FACTORY.newInstance(getAddress(), port, 10_000, 128 * 1024, 128 * 1024, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(plugin);
		}
		checkForUpdates();
		Bukkit.getPluginManager().registerEvents(new IGUIManager(), this);
	}
	
	private final int SPIGOT_RESOURCE_ID = 89796;
	
	private void checkForUpdates() {
		new UpdateChecker(this, UpdateCheckSource.SPIGOT, SPIGOT_RESOURCE_ID + "") 
			.setUsedVersion("v" + getDescription().getVersion())
			.setDownloadLink(SPIGOT_RESOURCE_ID)
			.setChangelogLink(SPIGOT_RESOURCE_ID)
            .setNotifyOpsOnJoin(true)
			.checkEveryXHours(6)
			.checkNow();
	}
	
	public void send(Packet packet) {
		if(asyncClient != null)
			asyncClient.write(packet);
	}
	
	public static ILibrary getInstance() {
		return plugin;
	}
	
	public boolean canSendPacket() {
		return asyncClient != null && asyncClient.isConnected();
	}
	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return f.getString("address");
	}
	
	@Override
	public synchronized void onDisable() {
		try {
			asyncClient.setConnected(false);
			asyncClient.onDisconnect(null);
		} catch (Throwable e) {}
			
	}
	
	public void registerEvents(Listener iListener, Object owner) {
		EventManager.registerEvents(iListener, owner);
	}
	
	public <T extends Event> T callEvent(T event) {
		return EventManager.callEvent(event);
	}
	
	public void registerEvent(Class<? extends Event> iEvent, Order priority, EventExecutor executor, Object owner) {
        EventManager.registerEvent(iEvent, priority, executor, owner);
    }
	
	public void unregisterAllEvents() {
		HandlerList.unregisterAll();
	}
	
	public void unregisterAllEvents(Object plugin) {
		HandlerList.unregisterAll(plugin);
	}
}