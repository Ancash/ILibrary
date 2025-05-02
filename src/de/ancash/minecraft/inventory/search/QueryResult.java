package de.ancash.minecraft.inventory.search;

import org.bukkit.inventory.ItemStack;

public class QueryResult {

	private final ItemStack item;
	private final Runnable onClick;

	public QueryResult(ItemStack item, Runnable onClick) {
		this.item = item;
		this.onClick = onClick;
	}

	public void onClick() {
		onClick.run();
	}

	public ItemStack getItem() {
		return item;
	}

}
