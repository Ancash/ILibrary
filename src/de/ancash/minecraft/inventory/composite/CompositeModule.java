package de.ancash.minecraft.inventory.composite;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;

public class CompositeModule {

	protected final CompositeIGUIModuleWrapper module;

	public CompositeModule(CompositeGUI gui, ItemStack item, int slot,
			Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick) {
		this.module = new CompositeIGUIModuleWrapper(gui, item, slot, onClick);
	}

	public void setCanBeEnabled(Supplier<Boolean> enable) {
		module.setCanBeEnabled(enable);
	}

	public void setOnDisable(Consumer<CompositeGUI> onDisable) {
		module.setOnDisable(onDisable);
	}

	public void setOnEnable(Consumer<CompositeGUI> onEnable) {
		module.setOnEnable(onEnable);
	}

	public void setOnUpdate(Consumer<CompositeGUI> onUpdate) {
		module.setOnUpdate(onUpdate);
	}

	public void setOnDisabledClick(Consumer<CompositeGUI> onDisabledClick) {
		module.setOnDisabledClick(onDisabledClick);
	}

	public int getSlot() {
		return module.getSlot();
	}

	public ItemStack getItem() {
		return module.getItem();
	}
}
