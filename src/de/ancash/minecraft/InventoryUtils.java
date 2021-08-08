package de.ancash.minecraft;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Tuple;
import de.ancash.datastructures.tuples.Unit;

public class InventoryUtils {

	public static int getFreeSlots(Inventory inventory) {
		int free = 0;
		for(int i = 0; i<inventory.getSize(); i++) {
			if(inventory.getItem(i) == null || inventory.getItem(i).getType().equals(XMaterial.AIR.parseMaterial())) free++;
		}
		return free;
	}
	
	public static int getFreeSpaceExact(Inventory inventory, ItemStack is) {
		final Unit<Integer> space = Tuple.of(new Integer(getFreeSlots(inventory) * is.getMaxStackSize()));
		SerializableItemStack sis = new SerializableItemStack(is);
		Arrays.asList(inventory.getContents()).stream().filter(item -> item != null).filter(item -> sis.equalsIgnoreAmount(new SerializableItemStack(item))).forEach(item -> space.setFirst(space.getFirst() + (item.getMaxStackSize() - item.getAmount())));
		return space.getFirst().intValue();
	}
	
	public static void addItemAmount(int i, ItemStack is, Player p) {
		SerializableItemStack sis = new SerializableItemStack(is);
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
			
			if(sis.equalsIgnoreAmount(new SerializableItemStack(inv))) {
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
	
	public static void removeItemAmount(int i, ItemStack is, Player p) {
		SerializableItemStack sis = new SerializableItemStack(is);
		for(int s = 0; s<p.getInventory().getSize(); s++) {
			ItemStack item = p.getInventory().getItem(s);
			if(item == null || !sis.equalsIgnoreAmount(new SerializableItemStack(item))) continue;
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
		SerializableItemStack sis = new SerializableItemStack(is);
		for(int t = 0; t<inv.getSize(); t++) {
			ItemStack cont = inv.getItem(t);
			if(cont == null || cont.getType().equals(XMaterial.AIR.parseMaterial())) 
				continue;
			if(sis.equalsIgnoreAmount(new SerializableItemStack(cont))) {
				i += cont.getAmount();
			}
		}
		return i;
	}
	
}
