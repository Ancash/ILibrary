package de.ancash.nbtnexus.stacker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ancash.ILibrary;
import de.ancash.nbtnexus.serde.SerializedItem;

public class InventoryStackListener implements Listener {

	private final Map<UUID, Integer> cooldown = new HashMap<>();

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onStack(InventoryClickEvent event) {
		Inventory clicked = event.getClickedInventory();
		if (clicked == null || event.getInventory().equals(clicked) || event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
			return;
		ItemStack cursor = event.getCursor();
		ItemStack slotItem = clicked.getItem(event.getSlot());
		if (slotItem == null || slotItem.getType() == Material.AIR || cursor == null || cursor.getType() == Material.AIR)
			return;
		if (slotItem.getAmount() == slotItem.getMaxStackSize())
			return;
		SerializedItem serializedCursor = SerializedItem.of(cursor);
		SerializedItem serializedSlotItem = SerializedItem.of(slotItem);
		if (!serializedCursor.areEqualIgnoreAmount(serializedSlotItem))
			return;
		InventoryAction action = event.getAction();
		if (action != InventoryAction.PLACE_ALL && action != InventoryAction.PLACE_ONE && action != InventoryAction.SWAP_WITH_CURSOR
				&& action != InventoryAction.PLACE_SOME)
			return;
		event.setCancelled(true);
		if (cooldown.containsKey(event.getWhoClicked().getUniqueId()) && cooldown.get(event.getWhoClicked().getUniqueId()) + 2 > ILibrary.getTick()) {
			return;
		}
		int adding = Math.min(slotItem.getMaxStackSize() - slotItem.getAmount(), cursor.getAmount());
		if (action == InventoryAction.PLACE_ONE)
			adding = 1;
		slotItem.setAmount(slotItem.getAmount() + adding);
		if (cursor.getAmount() - adding == 0)
			event.setCursor(null);
		else
			cursor.setAmount(cursor.getAmount() - adding);
		cooldown.put(event.getWhoClicked().getUniqueId(), ILibrary.getTick());
	}
}
