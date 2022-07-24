package de.ancash.minecraft.inventory.input;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import de.ancash.minecraft.inventory.IGUI;

public class ItemInputIGUI extends IGUI{
	
	private final ItemInputSlots slots;
	private Consumer<ItemStack[]> onInput;
	
	public ItemInputIGUI(ItemInputSlots slots, UUID id, int size, String title) {
		super(id, size, title);
		this.slots = slots;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
			event.setCancelled(true);
		} else if(event.getInventory().equals(event.getClickedInventory())) {
			if(!slots.getInputSlots().contains(event.getSlot())) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		if(onInput != null)
			onInput.accept(getInput());
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.getRawSlots().stream().filter(raw -> raw < event.getInventory().getSize() && !slots.getInputSlots().contains(raw)).findAny().ifPresent(raw -> event.setCancelled(true));
	}

	public void setOnInput(Consumer<ItemStack[]> onInput) {
		this.onInput = onInput;
	}	
	
	public ItemStack[] getInput() {
		return slots.getInputSlots().stream().map(this::getItem).toArray(ItemStack[]::new);
	}
}