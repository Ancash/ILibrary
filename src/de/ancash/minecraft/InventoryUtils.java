package de.ancash.minecraft;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Tuple;
import de.ancash.datastructures.tuples.Unit;

public class InventoryUtils {

	public static int getFreeSlots(Inventory inventory) {
		int free = 0;
		for(int i = 0; i<36; i++) {
			if(inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR)) free++;
		}
		return free;
	}
	
	public static int getFreeSpaceExact(Inventory inventory, ItemStack is) {
		final Unit<Integer> space = Tuple.of(new Integer(getFreeSlots(inventory) * is.getMaxStackSize()));
		Arrays.asList(inventory.getContents()).stream().filter(item -> item != null).filter(item -> ItemStackUtils.isSimilar(item, is)).forEach(item -> space.setFirst(space.getFirst() + (item.getMaxStackSize() - item.getAmount())));
		return space.getFirst().intValue();
	}
	
	public static void addItemAmount(int i, ItemStack is, Player p) {
		for(int s = 0; s<p.getInventory().getSize(); s++) {
			if(i == 0) return;
			ItemStack inv = p.getInventory().getItem(s);
			if(ItemStackUtils.isSimilar(is, inv)) {
				if(inv.getAmount() == 64) continue;
				int canAdd= 64 - inv.getAmount();
				if(canAdd >= i) {
					inv.setAmount(inv.getAmount() + i);
					return;
				}
				if(canAdd < i) {
					inv.setAmount(64);
					i = i - canAdd;
					continue;
				}
				
			}
			if(inv == null || inv.getType().equals(Material.AIR)) {
				if(i >= 64) {
					ItemStack t = is.clone();
					t.setAmount(64);
					p.getInventory().addItem(t);
					i = i - 64;
					continue;
				}
				ItemStack t = is.clone();
				t.setAmount(i);
				p.getInventory().addItem(t);
				return;
			}
		}
	}
	
	public static void removeItemAmount(int i, ItemStack is, Player p) {
		for(int s = 0; s<p.getInventory().getSize(); s++) {
			ItemStack item = p.getInventory().getItem(s);
			if(item == null || !ItemStackUtils.isSimilar(is, item)) continue;
			int dif = i;
			if(dif > item.getAmount()) {
				dif = item.getAmount();
			}
			if(item.getAmount() == dif) {
				p.getInventory().setItem(s, null);
			} else {
				item.setAmount(item.getAmount() - dif);
			}
			i -= dif;
			if(i == 0) break;
		}
	}
	
	public static int getContentAmount(Inventory inv, ItemStack is) {
		int i = 0;
		for(int t = 0; t<inv.getSize(); t++) {
			ItemStack cont = inv.getItem(t);
			if(cont == null || cont.getType().equals(Material.AIR)) 
				continue;
			if(ItemStackUtils.isSimilar(is, cont)) {
				i += cont.getAmount();
			}
		}
		return i;
	}
	
}
