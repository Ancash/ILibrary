package de.ancash.minecraft.inventory;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import de.ancash.datastructures.maps.CompactMap;

public class IGUIManager implements Listener{

	private static final CompactMap<UUID, IGUI> registeredIGUIs = new CompactMap<>();
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		registeredIGUIs.entrySet().stream().filter(entry -> entry.getKey().equals(event.getWhoClicked().getUniqueId())).forEach(igui -> igui.getValue().preOnInventoryClick(event));
	}
	
	@EventHandler
	public void inventoryDragEvent(InventoryDragEvent event) {
		registeredIGUIs.entrySet().stream().filter(entry -> entry.getKey().equals(event.getWhoClicked().getUniqueId())).forEach(igui -> igui.getValue().preOnInventoryDrag(event));
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent event) {
		registeredIGUIs.entrySet().stream().filter(entry -> entry.getKey().equals(event.getPlayer().getUniqueId())).forEach(igui -> igui.getValue().preOnInventoryClose(event));
	}
	
	/**
	 * Registering will automatically call event methods:
	 * {@link IGUI#onInventoryClick(InventoryClickEvent)}
	 * {@link IGUI#onInventoryClose(InventoryCloseEvent)}
	 * {@link IGUI#onInventoryDrag(InventoryDragEvent)}
	 * 
	 * @param player
	 * @param igui
	 */
	public static void register(IGUI igui, UUID uuid) {
		registeredIGUIs.put(uuid, igui);
	}
	
	/**
	 * Unregisters {@link IGUI}
	 * 
	 * @param player
	 */
	public static void remove(UUID uuid) {
		registeredIGUIs.remove(uuid);
	}
}
