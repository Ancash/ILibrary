package de.ancash;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;

import de.ancash.libs.org.bukkit.event.Event;
import de.ancash.libs.org.bukkit.event.EventExecutor;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.HandlerList;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.libs.org.bukkit.event.Order;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.minecraft.crafting.ICraftingManager;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.updatechecker.UpdateCheckSource;
import de.ancash.minecraft.updatechecker.UpdateChecker;
import de.ancash.misc.FileUtils;
import de.ancash.sockets.async.AsyncChatClient;
import de.ancash.sockets.async.AsyncChatClientFactory;
import de.ancash.sockets.packet.Packet;

public class ILibrary extends JavaPlugin{

	private AsyncChatClient asyncClient;
	private int port;
	private static ILibrary plugin;
	private YamlFile f;

	@Override
	public void onEnable() {		
		plugin = this;
		f = new YamlFile(new File("plugins/ILibrary/config.yml"));
		try {
			if(!f.exists()) 
				FileUtils.copyInputStreamToFile(getResource("config.yml"), new File(f.getFilePath()));
			
			f.load();
			ICraftingManager.getSingleton().init(this);
			port = f.getInt("port");
			if(f.getBoolean("chat-client")) {
				new BukkitRunnable() {
					
					@Override
					public void run() {
						try {
							asyncClient = new AsyncChatClientFactory().newInstance(getAddress(), port, 8 * 1024, port, 4);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(plugin);
			}
			checkForUpdates();
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
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
	
	public void send(Packet packet) throws IOException {
		if(asyncClient != null)
			try {
				asyncClient.write(packet);
			} catch (IOException | InterruptedException e) {
				throw new IOException(e);
			}
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