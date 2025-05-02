package de.ancash.minecraft.inventory.composite;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;

public class CompositeModuleSupplier {

	protected final List<Integer> slots;
	protected final ItemStack item;
	protected final Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick;

	CompositeModuleSupplier(CompositeGUI gui, ItemStack item, List<Integer> slots, Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick) {
		this.slots = Collections.unmodifiableList(slots);
		this.item = item.clone();
		this.onClick = onClick;
	}

	public CompositeModule newInstance(CompositeGUI gui) {
		return new CompositeModule(gui, item.clone(), slots, onClick);
	}
}
