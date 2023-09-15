package de.ancash.minecraft.inventory.composite;

import org.bukkit.inventory.ItemStack;

public class StaticCompositeModule extends CompositeModule {

	public StaticCompositeModule(CompositeGUI gui, ItemStack item, int slot) {
		super(gui, item, slot, null);
	}

}
