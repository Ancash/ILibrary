package de.ancash;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

import de.ancash.events.IEvent;
import de.ancash.events.IEventHandler;
import de.ancash.events.IHandlerList;
import de.ancash.events.IListener;
import de.ancash.events.ChatClientPacketReceiveEvent;
import de.ancash.events.EventExecutor;
import de.ancash.events.EventManager;
import de.ancash.events.Order;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.misc.FileUtils;
import de.ancash.misc.MathsUtils;
import de.ancash.sockets.client.ChatClient;
import de.ancash.sockets.packet.Packet;
import de.ancash.sockets.server.NIOServer;
import de.ancash.yaml.configuration.file.YamlFile;
import de.ancash.yaml.exceptions.InvalidConfigurationException;

public class ILibrary extends JavaPlugin implements IListener{

	private ChatClient client;
	private NIOServer server;
	private int port;
	private static ILibrary plugin;
	private YamlFile f;
	
	private static final short SPEEDTEST_MULTI_START = (short) 1001;
	private static final short SPEEDTEST_MULTI_HEADER = (short) 1002;
	
	@Override
	public void onEnable() {
		Packet.addHeader(SPEEDTEST_MULTI_HEADER);
		Packet.addHeader(SPEEDTEST_MULTI_START);
		plugin = this;
		f = new YamlFile(new File("plugins/ILibrary/config.yml"));
		try {
			if(!f.exists()) {
				FileUtils.copyInputStreamToFile(getResource("config.yml"), new File(f.getFilePath()));
			}
			f.load();
			port = f.getInt("port");
			if(f.getBoolean("server")) {
				try {
					server = new NIOServer(getAddress(), port, get(f.getString("server-wait-strategy")));
					server.start();
				} catch(Throwable th) {
					System.err.println("Could not start server. " + getAddress() + ", " + port + ": " + th);
				}
			} else {
				server = null;
			}
			if(f.getBoolean("chat-client")) {
				client = new ChatClient(getAddress(), port, this.getName(), 1, get(f.getString("client-wait-strategy")));
			}
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
		Bukkit.getPluginManager().registerEvents(new IGUIManager(), this);
		/*registerEvents(this, this);
		
		new BukkitRunnable() {
			
			long send;
			
			@Override
			public void run() {
				StoragePacket sp = new StoragePacket(StorageAction.GET_LONG, "test", "long", null, new StorageCallback() {
					
					@Override
					public void call(Object arg0) {
						System.out.println("Called callback. Result: " + arg0 + " in " + (System.nanoTime() - send) + " ns");
					}
				});
				try {
					send = System.nanoTime();
					send(sp.getPacket());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.runTaskTimer(plugin, 20, 10);*/
		/*
		Packet p = new Packet(NIOServer.PING_HEADER);
		p.isClientTarget(false);
		if(server == null)
			new BukkitRunnable() {
			
				@Override
				public void run() {
					try {
						send(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.runTaskTimer(plugin, 20 * 5, 5);
		
		*/
		/*
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(server != null) return;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("start");
				try {
					if(server == null)
						speedtest(sending);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("done");
			}
		}, "ILibrary-Warmup").start();*/
	}
	
	private WaitStrategy get(String str) {
		WaitStrategy wait = new YieldingWaitStrategy();
		switch (str.toLowerCase()) {
		case "yielding":
			wait = new YieldingWaitStrategy();
			break;
		case "blocking":
			wait = new BlockingWaitStrategy();
			break;
		case "sleeping":
			wait = new SleepingWaitStrategy();
			break;
		case "busy":
			wait = new BusySpinWaitStrategy();
			break;
		default:
			break;
		}
		return wait;
	}
	
	
	private final int sending = 100000000;
	
	private AtomicLong total = new AtomicLong();
	private long start;
	private int shouldReceive;
	
	public void speedtest(long msgs) throws IOException {
		if(canSendPacket()) {
			start = System.nanoTime();
			total.set(0);
			Packet packet = new Packet(SPEEDTEST_MULTI_START);
			packet.setSerializable(sending);
			send(packet);
			packet = new Packet(SPEEDTEST_MULTI_HEADER);
			for(long i = 0; i<msgs; i++) {
				send(packet);
			}
		}
	}
	
	@IEventHandler
	public void onPacketReceive(ChatClientPacketReceiveEvent event) {
		switch (event.getPacket().getHeader()) {
		case NIOServer.PING_HEADER:
			System.out.println("Ping: " + (System.nanoTime() - event.getPacket().getTimeStamp()) + " ns");
			return;
		case SPEEDTEST_MULTI_START:
			start = System.nanoTime();
			total.set(0);
			shouldReceive = (int) event.getPacket().getSerializable();
			System.out.println("Registered start of speed test. Expecting " + shouldReceive + " packets!");
			return;	
		case SPEEDTEST_MULTI_HEADER:
			if(total.incrementAndGet() % 100000 == 0) {
				long bytes = total.get() * 12;
				long nanoTime = System.nanoTime() - start;
				double time = MathsUtils.round((nanoTime) / 1000000000D, 5);
				System.out.println(total + " packets in " + bytes + " bytes in " + time + " s -> " + (long) (bytes / time) + " bytes/s");
			}
			if(total.get() == shouldReceive) {
				long bytes = total.get() * 12;
				long nanoTime = System.nanoTime() - start;
				double time = MathsUtils.round((nanoTime) / 1000000000D, 5);
				System.out.println("received all " + total + " packets in " + bytes + " bytes in " + time + " s -> " + (long) (bytes / time) + " bytes/s");
			}
			return;
		default:
			break;
		}
	}
	
	
	
	public boolean send(Packet msg) throws IOException {
		if(client != null) {
			return client.send(msg);
		}
		return false;
	}
	
	public static ILibrary getInstance() {
		return plugin;
	}
	
	public boolean isSocketNull() {
		return server == null;
	}
	
	public boolean canSendPacket() {
		return client != null && client.isConnected();
	}
	
	public int getDefaultSocketPort() {
		return port;
	}
	
	public String getAddress() {
		return f.getString("address");
	}
	
	public boolean newServerSocket() throws IOException {
		if(!isSocketNull()) return false;
		server = new NIOServer(getAddress(), port, new BlockingWaitStrategy());
		return true;
	}
	
	@Override
	public void onDisable() {
		try {
			client.stop();
		} catch (Throwable e) {}
		
		try {
			server.stop();
		} catch (Throwable e) {}	
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