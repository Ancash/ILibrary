package de.ancash.minecraft.inventory;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import de.ancash.misc.Validate;

public abstract class PagedIGUI extends IGUI{

	private ItemStack[] items;
	
	private ItemStack[][] sortedItems;
	private final int[] itemSlots;
	private int pages;
	private int currentPage;
	
	public PagedIGUI(UUID player, int size, String title, ItemStack[] items, int[] slots) {
		super(player, size, title);
		this.itemSlots = slots;
		this.items = items;
		sort();
	}

	public int currentPage() {
		return pages;
	}
	
	private void sort() {
		pages = 0;
		while(pages * itemSlots.length < items.length) pages++;
		sortedItems = new ItemStack[pages][itemSlots.length];
		currentPage = 1;
		for(int all = 0; all<items.length; all++) {
			if(all > 0 && all % itemSlots.length == 0) currentPage++;
			sortedItems[currentPage - 1][all - ((currentPage - 1) * itemSlots.length)] = items[all];
		}
	}
	
	public void newItems(ItemStack[] items) {
		this.items = items;
		sort();
	}
	
	public void openNextPage() {
		currentPage++;
		if(currentPage > pages) currentPage = pages;
		open(currentPage);
	}
	
	public void open(int page) {
		Validate.isTrue(page > 0 && page <= pages);
		currentPage = page;
		for(int i : itemSlots) {
			setItem(null, i);
			removeInventoryItem(i);
		}
		for(int i = 0; i<itemSlots.length; i++) {
			setItem(sortedItems[currentPage - 1][i], itemSlots[i]);
		}
	}
}
