package de.ancash.ilibrary;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.ilibrary.events.AEvent;
import de.ancash.ilibrary.events.AHandlerList;
import de.ancash.ilibrary.events.AListener;
import de.ancash.ilibrary.events.EventExecutor;
import de.ancash.ilibrary.events.EventManager;
import de.ancash.ilibrary.events.Order;
import de.ancash.ilibrary.sockets.NIOServer;
import de.ancash.ilibrary.yaml.configuration.file.YamlFile;
import de.ancash.ilibrary.yaml.exceptions.InvalidConfigurationException;

public class ILibrary extends JavaPlugin{
	
	//TestServer ts;
	//TestClient tc;
	
	private static NIOServer server;
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

		if(server != null)
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void registerEvents(AListener aListener, Object owner) {
		EventManager.registerEvents(aListener, owner);
	}
	
	public <T extends AEvent> T callEvent(T event) {
		return EventManager.callEvent(event);
	}
	
	public void registerEvent(Class<? extends AEvent> aEvent, Order priority, EventExecutor executor, Object owner) {
        EventManager.registerEvent(aEvent, priority, executor, owner);
    }
	
	public void unregisterAllEvents() {
		AHandlerList.unregisterAll();
	}
	
	public void unregisterAllEvents(Object plugin) {
		AHandlerList.unregisterAll(plugin);
	}
}