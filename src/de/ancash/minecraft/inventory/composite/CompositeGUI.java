package de.ancash.minecraft.inventory.composite;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;

public class CompositeGUI {

	protected final CompositeIGUI gui;
	protected final Map<Integer, CompositeModule> modules = new HashMap<>();
	protected boolean enabled = false;

	public CompositeGUI(UUID id, int size, String title) {
		this.gui = new CompositeIGUI(id, size, title);
	}

	public UUID getId() {
		return gui.getId();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(getId());
	}

	public int getSize() {
		return gui.getSize();
	}

	public String getTitle() {
		return gui.getTitle();
	}

	@SuppressWarnings("nls")
	public synchronized void enable() {
		if (enabled)
			throw new IllegalStateException("already enabled");
		enabled = true;
		modules.values().forEach(cm -> cm.modules.forEach(CompositeIGUIModuleWrapper::enable));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public CompositeModule removeModule(int slot) {
		if (!modules.containsKey(slot))
			return null;
		CompositeModule cm = modules.remove(slot);
		if (cm == null)
			return null;
		gui.removeModule(slot);
		return cm;
	}

	@SuppressWarnings("nls")
	public void addModule(CompositeModule m) {
		for (int s : m.getSlots()) {
			if (modules.containsKey(s))
				throw new IllegalStateException(s + " already occupied");
			if (s < 0 || s >= getSize())
				throw new IllegalArgumentException("out of index: " + s);
		}
		for (int s : m.getSlots())
			modules.put(s, m);
		if (enabled)
			m.modules.forEach(c -> gui.addModule(c).enable());
	}

	class CompositeIGUI extends IGUI {

		public CompositeIGUI(UUID id, int size, String title) {
			super(id, size, title);
		}

		@Override
		public void onInventoryClick(InventoryClickEvent event) {
			event.setCancelled(true);
		}

		@Override
		public void onInventoryClose(InventoryCloseEvent event) {
			IGUIManager.remove(getId());
		}

		@Override
		public void onInventoryDrag(InventoryDragEvent event) {
			event.setCancelled(true);
		}

	}

}
