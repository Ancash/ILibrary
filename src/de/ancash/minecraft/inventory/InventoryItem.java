package de.ancash.minecraft.inventory;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.ancash.minecraft.nbt.NBTItem;

public class InventoryItem extends NBTItem{
	
	protected final IGUI igui;
	protected final int slot;
	protected Clickable clickable;
	
	public InventoryItem(IGUI igui, ItemStack item, int slot, Clickable clickable) {
		super(item);
		this.igui = igui;
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
	
	/**
	 * Will be executed on {@link IGUI#onInventoryClick(InventoryClickEvent)}
	 * 
	 * @param shift
	 * @param action
	 */
	final void onClick(int slot, boolean shift, InventoryAction action, boolean topInventorys) {
		if(clickable != null) 
			clickable.onClick(slot, shift, action, topInventorys);
	}
}
