package de.ancash.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

	public static int getFreeSlots(ItemStack[] items) {
		int free = 0;
		for(int i = 0; i<items.length; i++)
			if(items[i] == null || items[i].getType().equals(XMaterial.AIR.parseMaterial()))
				free++;
		return free;
	}
	
	public static int getFreeSpaceExact(ItemStack[] items, ItemStack is) {
		int space = getFreeSlots(items) * is.getMaxStackSize();
		IItemStack sis = new IItemStack(is);
		for(int i = 0; i<items.length; i++) {
			ItemStack a = items[i];
			if(a == null || a.getType().equals(XMaterial.AIR.parseMaterial())) continue;
			if(sis.hashCode() == new IItemStack(a).hashCode())
				space += a.getMaxStackSize() - a.getAmount();
				
		}
		return space;
	}
	
	public static void addItemAmount(int i, ItemStack is, Player p) {
		IItemStack sis = new IItemStack(is);
		for(int s = 0; s<p.getInventory().getSize(); s++) {
			if(i == 0) return;
			ItemStack inv = p.getInventory().getItem(s);
			
			if(inv == null || inv.getType().equals(XMaterial.AIR.parseMaterial())) {
				if(i >= is.getMaxStackSize()) {
					ItemStack t = is.clone();
					t.setAmount(is.getMaxStackSize());
					p.getInventory().addItem(t);
					i = i - is.getMaxStackSize();
					continue;
				}
				ItemStack t = is.clone();
				t.setAmount(i);
				p.getInventory().setItem(s, t);
				return;
			}
			
			if(sis.hashCode() == new IItemStack(inv).hashCode()) {
				if(inv.getAmount() == inv.getMaxStackSize()) continue;
				int canAdd= inv.getMaxStackSize() - inv.getAmount();
				if(canAdd > i) {
					inv.setAmount(inv.getAmount() + i);
					return;
				}
				if(canAdd <= i) {
					inv.setAmount(inv.getMaxStackSize());
					i = i - canAdd;
					continue;
				}
				
			}
		}
	}
	
	public static int removeItemAmount(int i, ItemStack is, Player p) {
		i = is.getAmount() * i;
		IItemStack sis = new IItemStack(is);
		for(int s = 0; s<p.getInventory().getSize(); s++) {
			ItemStack item = p.getInventory().getItem(s);
			if(item == null || sis.hashCode() != new IItemStack(item).hashCode()) continue;
			if(item.getAmount() <= i) {
				i -= item.getAmount();
				p.getInventory().setItem(s, null);
			} else {
				item.setAmount(item.getAmount() - i);
				i = 0;
			}
			if(i == 0) break;
		}
		return i;
	}
	
	public static int getContentAmount(ItemStack[] items, ItemStack is) {
		int i = 0;
		IItemStack sis = new IItemStack(is);
		for(int t = 0; t<items.length; t++) {
			ItemStack cont = items[t];
			if(cont == null || cont.getType().equals(XMaterial.AIR.parseMaterial())) 
				continue;
			if(sis.hashCode() == new IItemStack(cont).hashCode())
				i += cont.getAmount();
		}
		return (int) Math.floor(i / is.getAmount());
	}
	
}
