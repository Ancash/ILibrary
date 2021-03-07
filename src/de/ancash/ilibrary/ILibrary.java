package de.ancash.ilibrary;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.ilibrary.events.IEvent;
import de.ancash.ilibrary.events.IHandlerList;
import de.ancash.ilibrary.events.IListener;
import de.ancash.ilibrary.events.EventExecutor;
import de.ancash.ilibrary.events.EventManager;
import de.ancash.ilibrary.events.Order;
import de.ancash.ilibrary.sockets.NIOClient;
import de.ancash.ilibrary.sockets.NIOServer;
import de.ancash.ilibrary.sockets.Packet;
import de.ancash.ilibrary.yaml.configuration.file.YamlFile;
import de.ancash.ilibrary.yaml.exceptions.InvalidConfigurationException;

public class ILibrary extends JavaPlugin{
	
	//TestServer ts;
	//TestClient tc;
	
	private static NIOServer server;
	private static NIOClient client;
	private int port;
	private static ILibrary plugin;
	private YamlFile f;
	
	@Override
	public void onEnable() {
		plugin = this;

		f = new YamlFile(new File("plugins/ILibrary/config.yml"));
		if(!f.exists()) {
			try {
				f.createNewFile(false);
				f.load();
				f.set("defaultSocket", true);
				f.set("port", 25600);
				f.set("address", "localhost");
				f.save();
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		try {
			f.load();
			port = f.getInt("port");
			if(f.getBoolean("defaultSocket")) {
				server = new NIOServer(f.getString("address"), port);
				server.start();
			} else {
				server = null;
			}
			f.save();
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
		client = new NIOClient(f.getString("address"), port, this.getClass().getCanonicalName());
	}
	
	public boolean sendPacket(Packet p) {
		if(client != null && client.isActive()) {
			client.send(p);
			return true;
		}
		return false;
	}
	
	public static ILibrary getInstance() {
		return plugin;
	}
	
	public boolean isDefaultSocketRunning() {
		return server != null;
	}
	
	public int getDefaultSocketPort() {
		return port;
	}
	
	public String getAddress() {
		return f.getString("address");
	}
	
	public boolean newServerSocket() throws IOException {
		if(isDefaultSocketRunning()) return false;
		server = new NIOServer("localhost", port);
		return true;
	}
	
	@Override
	public void onDisable() {
		
		if(client != null) {
			client.stop();
		}
		
		if(server != null)
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void registerEvents(IListener iListener, Object owner) {
		EventManager.registerEvents(iListener, owner);
	}
	
	public <T extends IEvent> T callEvent(T event) {
		return EventManager.callEvent(event);
	}
	
	public void registerEvent(Class<? extends IEvent> iEvent, Order priority, EventExecutor executor, Object owner) {
        EventManager.registerEvent(iEvent, priority, executor, owner);
    }
	
	public void unregisterAllEvents() {
		IHandlerList.unregisterAll();
	}
	
	public void unregisterAllEvents(Object plugin) {
		IHandlerList.unregisterAll(plugin);
	}
}