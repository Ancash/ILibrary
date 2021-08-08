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
		this(mat, amount, mat.getData());
	}
	
	public ItemBuilder(XMaterial mat, int amount, byte data) {
		this(mat, amount, data, (short) 0);
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuilder(XMaterial mat, int amount, byte data, short durability) {
		this.item = new ItemStack(mat.parseMaterial(), amount, data);
		item.setDurability(durability);
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