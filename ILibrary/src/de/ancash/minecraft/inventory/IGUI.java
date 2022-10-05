package de.ancash.minecraft.inventory;

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

	/**
	 * Constructor for IGUI.
	 * 
	 * @param owner
	 * @param name
	 * @param size
	 */
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
		inv = Bukkit.createInventory(null, size, title);
	}

	public void open() {
		openTick = ILibrary.getTick();
		IGUIManager.register(this, id);
		Bukkit.getPlayer(id).openInventory(inv);
	}

	/**
	 * set the {@link UUID}
	 * 
	 * @param id
	 */
	public void setUUID(UUID id) {
		this.id = id;
	}

	/**
	 * Prevent the gui to call onClose on next inventory close event
	 * 
	 * @param b
	 */
	public void preventNextClose(boolean b) {
		this.closeOnNextClose = !b;
	}

	/**
	 * Closes the inventory for one viewer
	 * 
	 * @param human
	 */
	public void close(HumanEntity human) {
		close(human.getUniqueId());
	}

	/**
	 * Closes the inventory for one viewer
	 * 
	 * @param id
	 */
	public void close(UUID id) {
		inv.getViewers().stream().filter(viewer -> viewer.getUniqueId().equals(id)).findFirst()
				.ifPresent(HumanEntity::closeInventory);
	}

	/**
	 * Closes the inventory for all viewers
	 * 
	 */
	public void closeAll() {
		inv.getViewers().stream().collect(Collectors.toList()).forEach(HumanEntity::closeInventory);
	}

	/**
	 * Checks wether Item at slot is {@link InventoryItem}
	 * 
	 * @param slot
	 * @return {@link Boolean}
	 */
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

	/**
	 * Clears all InventoryItems
	 * 
	 */
	public final void clearInventoryItems() {
		inventoryItems = new InventoryItem[size];
	}

	/**
	 * Removes {@link InventoryItem} at slot
	 * 
	 * @param item
	 * @param slot
	 */
	public final void removeInventoryItem(int slot) {
		inventoryItems[slot] = null;
	}

	/**
	 * Get {@link InventoryItem} at slot May return null
	 * 
	 * @param slot
	 * @return {@link InventoryItem}
	 */
	public final InventoryItem getInventoryItem(int slot) {
		return inventoryItems[slot];
	}

	/**
	 * Set item in slot in inventory
	 * 
	 * @param is
	 * @param slot
	 */
	public final void setItem(ItemStack is, int slot) {
		inv.setItem(slot, is);
	}

	/**
	 * Get {@link ItemStack} at slot
	 * 
	 * @param slot
	 * @return {@link ItemStack}
	 */
	public final ItemStack getItem(int slot) {
		return inv.getItem(slot);
	}

	/**
	 * Set the items in the inventory. Recommended to use
	 * {@link IGUI#clearInventoryItems()} after this
	 * 
	 * @param contents
	 */
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

	/**
	 * Get the inventory size
	 * 
	 * @return {@link Integer}
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * Get the inventory title
	 * 
	 * @return {@link String}
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Will be called before {@link #onInventoryClick} and will call it after
	 * executing {@link InventoryItem#onClick} if possible
	 * 
	 * @param event
	 */
	final void preOnInventoryClick(InventoryClickEvent event) {
		if (isInventoryItem(event.getSlot()))
			getInventoryItem(event.getSlot()).onClick(event.getSlot(), event.isShiftClick(), event.getAction(),
					event.getInventory().equals(event.getClickedInventory()));
		onInventoryClick(event);
	}

	/**
	 * Will be called before {@link #onInventoryClose} and will call it
	 * 
	 * @param event
	 */
	final void preOnInventoryClose(InventoryCloseEvent event) {
		if (ILibrary.getTick() == openTick || ILibrary.getTick() - 1 == openTick)
			return;
		if (!closeOnNextClose) {
			closeOnNextClose = true;
		} else {
			onInventoryClose(event);
		}
	}

	/**
	 * Will be called before {@link #onInventoryDrag} and will call it
	 * 
	 * @param event
	 */
	final void preOnInventoryDrag(InventoryDragEvent event) {
		onInventoryDrag(event);
	}

	/**
	 * Called automatically on {@link InventoryClickEvent}
	 * 
	 * @param event
	 */
	public abstract void onInventoryClick(InventoryClickEvent event);

	/**
	 * Called automatically on {@link InventoryCloseEvent}
	 * 
	 * @param event
	 */
	public abstract void onInventoryClose(InventoryCloseEvent event);

	/**
	 * Called automatically on {@link InventoryDragEvent}
	 * 
	 * @param event
	 */
	public abstract void onInventoryDrag(InventoryDragEvent event);

	/**
	 * Get the id of this igui (player uuid)
	 * 
	 * @return
	 */
	public UUID getId() {
		return id;
	}
}
