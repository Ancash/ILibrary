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
import de.ancash.ilibrary.sockets.Answer;
import de.ancash.ilibrary.sockets.ChatServer;
import de.ancash.ilibrary.sockets.InfoPacket;
import de.ancash.ilibrary.sockets.Request;
import de.ancash.ilibrary.yaml.configuration.file.YamlFile;
import de.ancash.ilibrary.yaml.exceptions.InvalidConfigurationException;

public class ILibrary extends JavaPlugin{
	
	//TestServer ts;
	//TestClient tc;
	
	private static ChatServer server;
	private int port;
	private static ILibrary plugin;
	private YamlFile f;
	
	@Override
	public void onEnable() {
		plugin = this;
		//ts = new TestServer(25600, 1, "ALibrary");
		//tc = new TestClient("localhost", 25600, "ALibrary");
		//tc.send(new Request("ALibrary", "Ping", TargetType.SERVER));
		f = new YamlFile(new File("plugins/ILibrary/config.yml"));
		if(!f.exists()) {
			try {
				f.createNewFile(false);
				f.load();
				f.set("defaultSocket", true);
				f.set("port", 25600);
				f.set("clientCount", 10);
				f.save();
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		try {
			f.load();
			port = f.getInt("port");
			if(f.getBoolean("defaultSocket")) {
				server = new ChatServer(f.getInt("port"), f.getInt("clientCount"), "ILibrary") {
					
					@Override
					public void onRequest(int id, Request packet) {
						System.out.println("Received Request from " + packet.getOwner() + " but don't know what to do!");
					}
					
					@Override
					public void onAnswer(int id, Answer packet) {
						System.out.println("Received Answer from " + packet.getOwner() + " but don't know what to do!");
					}

					@Override
					public void onInfo(int id, InfoPacket packet) {
						System.out.println("Received Info from " + packet.getOwner() + " but don't know what to do!");
					}
				};
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
		return "localhost";
	}
	
	public boolean newServerSocket() {
		if(isDefaultSocketRunning()) return false;
		server = new ChatServer(port, f.getInt("clientCount"), "ILibrary") {
			
			@Override
			public void onRequest(int id, Request packet) {
				System.out.println("Received Request from " + packet.getOwner() + " but don't know what to do!");
			}
			
			@Override
			public void onAnswer(int id, Answer packet) {
				System.out.println("Received Answer from " + packet.getOwner() + " but don't know what to do!");
			}

			@Override
			public void onInfo(int id, InfoPacket packet) {
				System.out.println("Received Info from " + packet.getOwner() + " but don't know what to do!");
			}
		};
		return true;
	}
	
	@Override
	public void onDisable() {
		//tc.stop();
		//ts.stop();
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