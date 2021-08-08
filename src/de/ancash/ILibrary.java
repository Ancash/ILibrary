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
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.misc.FileUtils;
import de.ancash.sockets.client.ChatClient;
import de.ancash.sockets.packet.Packet;

public class ILibrary extends JavaPlugin{

	private ChatClient client;
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
			port = f.getInt("port");
			if(f.getBoolean("chat-client")) {
				client = new ChatClient(getAddress(), port, 1);
			}
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
		Bukkit.getPluginManager().registerEvents(new IGUIManager(), this);
	}
	
	public void send(Packet packet) throws IOException {
		if(client != null) {
			client.send(packet);
		}
	}
	
	public static ILibrary getInstance() {
		return plugin;
	}
	
	public boolean canSendPacket() {
		return client != null && client.isConnected();
	}
	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return f.getString("address");
	}
	
	@Override
	public void onDisable() {
		try {
			client.disconnect();
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