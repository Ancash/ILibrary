package de.ancash.minecraft.inventory;

import org.bukkit.event.inventory.InventoryAction;

public abstract interface Clickable {

	void onClick(int slot, boolean shift, InventoryAction action, boolean topInventory);

}
