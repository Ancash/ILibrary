package de.ancash.minecraft.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ancash.ILibrary;

public abstract class IGUI {

	protected Inventory inv;
	protected String title;
	protected int size;
	protected InventoryItem[] inventoryItems;
	protected UUID id;
	protected boolean closeOnNextClose = true;
	protected int openTick;
	protected final Map<Integer, IGUIModule> modules = new HashMap<>();
	protected int updateRate = 20;

	public IGUI(UUID id, int size, String title) {
		this.id = id;
		inv = Bukkit.createInventory(null, size, title);
		this.title = title;
		this.size = size;
		this.inventoryItems = new InventoryItem[size];
	}

	public void newInventory(String title, int size) {
		if (size % 9 != 0 || size <= 0)
			throw new IllegalArgumentException("Invalid size: " + size); //$NON-NLS-1$
		this.title = title;
		this.size = size;
		inventoryItems = new InventoryItem[size];
		modules.clear();
		inv = Bukkit.createInventory(null, size, title);
	}

	public void setUpdateRate(int i) {
		updateRate = i;
	}

	public int getUpdateRate() {
		return updateRate;
	}

	public void open() {
		openTick = ILibrary.getTick();
		IGUIManager.register(this, id);
		Bukkit.getPlayer(id).openInventory(inv);
	}

	public void setUUID(UUID id) {
		this.id = id;
	}

	public void preventNextClose(boolean b) {
		this.closeOnNextClose = !b;
	}

	public void close(HumanEntity human) {
		close(human.getUniqueId());
	}

	public void close(UUID id) {
		inv.getViewers().stream().filter(viewer -> viewer.getUniqueId().equals(id)).findFirst().ifPresent(HumanEntity::closeInventory);
	}

	public void closeAll() {
		inv.getViewers().stream().collect(Collectors.toList()).forEach(HumanEntity::closeInventory);
	}

	public final boolean isInventoryItem(int slot) {
		return slot >= 0 && slot < inventoryItems.length && inventoryItems[slot] != null;
	}

	public final void addInventoryItem(InventoryItem item) {
		setInventoryItem(item, item.getSlot());
	}

	public final void setInventoryItem(InventoryItem item, int slot) {
		inventoryItems[slot] = item;
		setItem(item.getItem(), slot);
	}

	public final void clearInventoryItems() {
		inventoryItems = new InventoryItem[size];
	}

	public final void removeInventoryItem(int slot) {
		inventoryItems[slot] = null;
	}

	public final InventoryItem getInventoryItem(int slot) {
		return inventoryItems[slot];
	}

	public final void setItem(ItemStack is, int slot) {
		inv.setItem(slot, is);
	}

	public final ItemStack getItem(int slot) {
		return inv.getItem(slot);
	}

	public final void setContents(ItemStack[] contents) {
		inv.setContents(contents);
	}

	/**
	 * Returns the inventory
	 * 
	 * @return {@link Inventory}
	 */
	public final Inventory getInventory() {
		return inv;
	}

	public final int getSize() {
		return size;
	}

	public final String getTitle() {
		return title;
	}

	final void preOnInventoryClick(InventoryClickEvent event) {
		if (isInventoryItem(event.getSlot()))
			getInventoryItem(event.getSlot()).onClick(event.getSlot(), event.isShiftClick(), event.getAction(),
					event.getInventory().equals(event.getClickedInventory()));
		if (checkModuleItems(event)) {
			event.setCancelled(true);
			return;
		}
		onInventoryClick(event);
	}

	protected boolean checkModuleItems(InventoryClickEvent event) {
		if (!event.getInventory().equals(event.getClickedInventory()))
			return false;
		IGUIModule m = modules.get(event.getSlot());
		if (m == null)
			return false;
		if (!m.isEnabled()) {
			m.onDisabledClick();
			return false;
		}
		m.preOnClick(event.getSlot(), event.isShiftClick(), event.getAction());
		return true;
	}

	final void preOnInventoryClose(InventoryCloseEvent event) {
		if (ILibrary.getTick() == openTick || ILibrary.getTick() - 1 == openTick)
			return;
		if (!closeOnNextClose) {
			closeOnNextClose = true;
		} else {
			onInventoryClose(event);
		}
	}

	final void preOnInventoryDrag(InventoryDragEvent event) {
		onInventoryDrag(event);
	}

	public abstract void onInventoryClick(InventoryClickEvent event);

	public abstract void onInventoryClose(InventoryCloseEvent event);

	public abstract void onInventoryDrag(InventoryDragEvent event);

	public UUID getId() {
		return id;
	}

	public <T extends IGUIModule> T addModule(T t) {
		t.slots.forEach(slot -> modules.put(slot, t));
		return t;
	}

	public Map<Integer, IGUIModule> getModules() {
		return Collections.unmodifiableMap(modules);
	}

	public void removeModule(int s) {
		IGUIModule m = modules.remove(s);
		if (m == null)
			return;
		m.disable();
		for (int i : m.slots) {
			modules.remove(i);
			inventoryItems[i] = null;
			setItem(null, i);
		}
	}

	public void disableAllModules() {
		modules.values().stream().filter(IGUIModule::isEnabled).forEach(IGUIModule::disable);
	}

	protected void updateModules() {
		for (IGUIModule mod : modules.values()) {
			if (!mod.isEnabled() && mod.canBeEnabled())
				mod.enable();
			if (!mod.isEnabled())
				continue;
			mod.onUpdate();
		}
	}
}
