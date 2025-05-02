package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;

public class StaticCompositeModule extends CompositeModule {

	public StaticCompositeModule(CompositeGUI gui, ItemStack item, List<Integer> slots,
			Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick) {
		super(gui, item, slots, onClick);
	}

}
