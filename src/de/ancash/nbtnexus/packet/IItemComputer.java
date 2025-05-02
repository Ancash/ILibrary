package de.ancash.nbtnexus.packet;

import org.bukkit.inventory.ItemStack;

public interface IItemComputer {

	public ItemStack computePlaceholder(ItemStack item);

	public ItemStack computeDefault(ItemStack original, ItemStack cur);
}
