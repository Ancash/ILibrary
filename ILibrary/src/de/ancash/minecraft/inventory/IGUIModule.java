package de.ancash.minecraft.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.inventory.InventoryAction;

public abstract class IGUIModule {

	protected boolean enabled = false;
	protected Set<Integer> slots = new HashSet<>();
	protected Map<Integer, IGUIModule.Clickable> clickables = new HashMap<>();
	protected final IGUI igui;

	public IGUIModule(IGUI igui) {
		this.igui = igui;
	}

	protected IGUIModule addSlots(int... slots) {
		for (int slot : slots)
			this.slots.add(slot);
		return this;
	}

	protected IGUIModule setClickable(int slot, IGUIModule.Clickable clickable) {
		clickables.put(slot, clickable);
		return this;
	}

	protected IGUIModule.Clickable getClickable(int slot) {
		return clickables.get(slot);
	}

	protected Set<Integer> getSlots() {
		return Collections.unmodifiableSet(slots);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void disable() {
		if (!isEnabled())
			return;
		enabled = false;
		onDisable();
	}

	public void enable() {
		if (isEnabled())
			return;
		enabled = true;
		onEnable();
	}

	protected abstract boolean canBeEnabled();
	
	protected abstract void onDisable();

	protected abstract void onEnable();

	protected abstract void onUpdate();

	final void preOnClick(int slot, boolean isShift, InventoryAction action) {
		if (!clickables.containsKey(slot))
			onClick(slot, isShift, action);
		else
			getClickable(slot).onClick(slot, isShift, action);
	}

	protected abstract void onClick(int slot, boolean isShift, InventoryAction inventoryAction);

	protected abstract void onDisabledClick();

	public abstract interface Clickable {

		void onClick(int slot, boolean shift, InventoryAction action);

	}

}
