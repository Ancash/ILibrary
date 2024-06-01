package de.ancash.minecraft.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import de.ancash.ILibrary;

public class PickupItemListener implements Listener{

	private static final Map<UUID, Integer> lastPickup = new HashMap<>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEvent(EntityPickupItemEvent event) {
		if(event.getEntityType() != EntityType.PLAYER)
			return;
		if(!event.isCancelled()) {
			lastPickup.put(event.getEntity().getUniqueId(), ILibrary.getTick());
		}
	}
	
	public static int getLastPickup(Player p) {
		return Optional.ofNullable(lastPickup.get(p.getUniqueId())).orElse(0);
	}
}
