package de.ancash.minecraft.inventory;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;

public class InventoryItem {

	protected final IGUI igui;
	protected int slot;
	protected Clickable clickable;
	protected NBTItem asNBT;
	protected ItemStack item;

	public InventoryItem(IGUI igui, ItemStack item, int slot, Clickable clickable) {
		this.igui = igui;
		this.item = item;
		this.slot = slot;
		this.clickable = clickable;
	}

	/**
	 * Set this item in the igui
	 * 
	 */
	public final void add() {
		igui.addInventoryItem(this);
	}

	/**
	 * Set {@link Clickable} which will be executed on {@link InventoryClickEvent}
	 * 
	 * @param clickable
	 */
	public final void setClickable(Clickable clickable) {
		this.clickable = clickable;
	}

	/**
	 * Returns the slot this is set at
	 * 
	 * @return
	 */
	public final int getSlot() {
		return slot;
	}

	/**
	 * Returns the igui this belongs to
	 * 
	 * @return
	 */
	public final IGUI getIGUI() {
		return igui;
	}

	public ItemStack getItem() {
		return asNBT == null ? item : asNBT.getItem();
	}

	public NBTItem getAsNBT() {
		return asNBT == null ? asNBT = new NBTItem(item) : asNBT;
	}

	/**
	 * Will be executed on {@link IGUI#onInventoryClick(InventoryClickEvent)}
	 * 
	 * @param shift
	 * @param action
	 */
	final void onClick(int slot, boolean shift, InventoryAction action, boolean topInventorys) {
		if (clickable != null)
			clickable.onClick(slot, shift, action, topInventorys);
	}
}
