package de.ancash.minecraft.inventory.composite;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.inventory.IGUIModule;

class CompositeIGUIModuleWrapper extends IGUIModule {

	private Supplier<Boolean> enable;
	private Consumer<CompositeGUI> onDisable;
	private Consumer<CompositeGUI> onEnable;
	private Consumer<CompositeGUI> onUpdate;
	private Consumer<CompositeGUI> onDisabledClick;
	private final Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick;
	private final CompositeGUI gui;
	private final ItemStack item;
	private final int slot;

	public CompositeIGUIModuleWrapper(CompositeGUI igui, ItemStack item, int slot, Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick) {
		super(igui.gui);
		this.gui = igui;
		this.onClick = onClick;
		this.slot = slot;
		this.item = item;
		addSlots(slot);
	}

	public void setCanBeEnabled(Supplier<Boolean> enable) {
		this.enable = enable;
	}

	@Override
	protected boolean canBeEnabled() {
		if (enable != null)
			return enable.get();
		return true;
	}

	@Override
	protected void onDisable() {
		if (onDisable != null)
			onDisable.accept(gui);
	}

	@Override
	protected void onEnable() {
		gui.gui.setItem(item, slot);
		if (onEnable != null)
			onEnable.accept(gui);
	}

	@Override
	protected void onUpdate() {
		if (onUpdate != null)
			onUpdate.accept(gui);
	}

	@Override
	protected void onClick(int slot, boolean isShift, InventoryAction inventoryAction) {
		if (onClick != null)
			onClick.accept(Tuple.of(slot, isShift, inventoryAction));
	}

	@Override
	protected void onDisabledClick() {
		if (onDisabledClick != null)
			onDisabledClick.accept(gui);
	}

	public void setOnDisable(Consumer<CompositeGUI> onDisable) {
		this.onDisable = onDisable;
	}

	public void setOnEnable(Consumer<CompositeGUI> onEnable) {
		this.onEnable = onEnable;
	}

	public void setOnUpdate(Consumer<CompositeGUI> onUpdate) {
		this.onUpdate = onUpdate;
	}

	public void setOnDisabledClick(Consumer<CompositeGUI> onDisabledClick) {
		this.onDisabledClick = onDisabledClick;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item.clone();
	}
}
