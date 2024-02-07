package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;

public class DynamicCompositeModule extends CompositeModule {

	protected final int updateInterval;

	public DynamicCompositeModule(CompositeGUI gui, ItemStack item, List<Integer> slots, Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick,
			int updateInterval) {
		super(gui, item, slots, onClick);
		this.updateInterval = updateInterval;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}
}
