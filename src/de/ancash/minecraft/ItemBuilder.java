package de.ancash.minecraft;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	private final ItemStack item;
	private final ItemMeta meta;
	
	public ItemBuilder(XMaterial mat) {
		this(mat, 1);
	}
	
	public ItemBuilder(XMaterial mat, int amount) {
		this.item = mat.parseItem();
		item.setAmount(amount);
		this.meta = item.getItemMeta();
	}
	
	public ItemBuilder setDisplayname(String name) {
		meta.setDisplayName(name);
		return this;
	}
	
	public ItemBuilder setLore(String...lore) {
		return setLore(Arrays.asList(lore));
	}
	
	public ItemBuilder setLore(List<String> lore) {
		meta.setLore(lore);
		return this;
	}
	
	public ItemStack build() {
		item.setItemMeta(meta);
		return item;
	}
}