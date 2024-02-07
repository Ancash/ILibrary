package de.ancash;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.libs.org.bukkit.event.Event;
import de.ancash.libs.org.bukkit.event.EventExecutor;
import de.ancash.libs.org.bukkit.event.EventManager;
import de.ancash.libs.org.bukkit.event.HandlerList;
import de.ancash.libs.org.bukkit.event.Listener;
import de.ancash.libs.org.bukkit.event.Order;
import de.ancash.minecraft.DefaultCompositeModuleParser;
import de.ancash.minecraft.crafting.ICraftingManager;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.composite.CompositeModuleRegistry;
import de.ancash.minecraft.updatechecker.UpdateCheckSource;
import de.ancash.minecraft.updatechecker.UpdateChecker;
import de.ancash.sockets.async.impl.packet.client.AsyncPacketClientFactory;

public class ILibrary extends JavaPlugin {

	public static final AsyncPacketClientFactory ASYNC_CHAT_CLIENT_FACTORY = new AsyncPacketClientFactory();

	private static ILibrary plugin;
	private static final AtomicInteger TICK = new AtomicInteger(0);

	public ILibrary() {
		plugin = this;
	}

	@Override
	public void onEnable() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> TICK.incrementAndGet(), 0, 1);
//		getCommand("fedit").setExecutor(new FEditCommand());
//		getCommand("ms").setExecutor(new MaterialSearchCommand());
		ICraftingManager.getSingleton().init(this);
		checkForUpdates();
		Bukkit.getPluginManager().registerEvents(new IGUIManager(this), this);
		CompositeModuleRegistry.register(this, new DefaultCompositeModuleParser());
	}

	private final int SPIGOT_RESOURCE_ID = 89796;

	private void checkForUpdates() {
		new UpdateChecker(this, UpdateCheckSource.SPIGOT, SPIGOT_RESOURCE_ID + "").setUsedVersion("v" + getDescription().getVersion())
				.setDownloadLink(SPIGOT_RESOURCE_ID).setChangelogLink(SPIGOT_RESOURCE_ID).setNotifyOpsOnJoin(true).checkEveryXHours(6).checkNow();
	}

	public static int getTick() {
		return TICK.get();
	}

	public static ILibrary getInstance() {
		return plugin;
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