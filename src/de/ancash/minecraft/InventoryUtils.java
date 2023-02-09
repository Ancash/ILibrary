package de.ancash.minecraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.ancash.minecraft.cryptomorin.xseries.XMaterial;

public class InventoryUtils {

	public static int getFreeSlots(ItemStack[] items) {
		int free = 0;
		for (int i = 0; i < items.length; i++)
			if (items[i] == null || items[i].getType().equals(XMaterial.AIR.parseMaterial()))
				free++;
		return free;
	}

	public static int countItemStack(Player player, ItemStack is) {
		IItemStack iis = new IItemStack(is);
		int cnt = 0;
		for (int s = 0; s < 36; s++) {
			ItemStack a = player.getInventory().getItem(s);
			if (a == null || a.getType() == Material.AIR)
				continue;
			if (new IItemStack(a).hashCode() == iis.hashCode())
				cnt += a.getAmount();
		}
		return cnt;
	}

	public static int getFreeSpaceExact(Player player, ItemStack is) {
		int cnt = 0;
		IItemStack iis = new IItemStack(is);
		for (int i = 0; i < 36; i++) {
			ItemStack a = player.getInventory().getItem(i);
			if (a == null || a.getType() == Material.AIR) {
				cnt += is.getMaxStackSize();
				continue;
			}
			if (iis.hashCode() == new IItemStack(a).hashCode())
				cnt += a.getMaxStackSize() - a.getAmount();
		}
		return cnt;
	}

	public static int addItemStack(Player player, ItemStack is, int amount) {
		IItemStack iis = new IItemStack(is);
		for (int s = 0; s < 36; s++) {
			if (amount == 0)
				return amount;
			ItemStack a = player.getInventory().getItem(s);
			if (a == null || a.getType() == Material.AIR) {
				ItemStack clone = is.clone();
				clone.setAmount(Math.min(amount, is.getMaxStackSize()));
				amount -= clone.getAmount();
				player.getInventory().setItem(s, clone);
			} else {
				IItemStack ais = new IItemStack(a);
				if (ais.hashCode() != iis.hashCode())
					continue;
				int left = a.getMaxStackSize() - a.getAmount();
				if (left == 0)
					continue;
				left = Math.min(left, amount);
				amount -= left;
				a.setAmount(a.getAmount() + left);
			}
		}
		return amount;
	}

	public static int removeItemStack(Player player, ItemStack is, int amount) {
		IItemStack iis = new IItemStack(is);
		for (int s = 0; s < 36; s++) {
			if (amount == 0)
				return amount;
			ItemStack a = player.getInventory().getItem(s);
			if (a == null || a.getType() == Material.AIR)
				continue;
			IItemStack ais = new IItemStack(a);
			if (ais.hashCode() != iis.hashCode())
				continue;
			int remove = Math.min(amount, a.getAmount());
			amount -= remove;
			if (a.getAmount() == remove)
				player.getInventory().setItem(s, null);
			else
				a.setAmount(a.getAmount() - remove);
		}
		return amount;
	}
}
