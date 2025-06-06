package de.ancash.nbtnexus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.serde.SerializedItem;

public class InventoryUtils {

	public static int getFreeSlots(ItemStack[] items) {
		int free = 0;
		for (int i = 0; i < items.length; i++)
			if (items[i] == null || items[i].getType().equals(XMaterial.AIR.parseMaterial()))
				free++;
		return free;
	}

	public static int countItemStack(Player player, ItemStack is) {
		return countItemStack(player, SerializedItem.of(is));
	}

	public static int countItemStack(Player player, SerializedItem si) {
		int cnt = 0;
		for (int s = 0; s < 36; s++) {
			ItemStack a = player.getInventory().getItem(s);
			if (a == null || a.getType() == Material.AIR)
				continue;
			if (SerializedItem.of(a).areEqualIgnoreAmount(si))
				cnt += a.getAmount();
		}
		return cnt;
	}

	public static int getFreeSpaceExact(Player player, ItemStack is) {
		return getFreeSpaceExact(player, SerializedItem.of(is));
	}

	public static int getFreeSpaceExact(Player player, SerializedItem si) {
		int cnt = 0;
		int maxStack = si.toItem().getMaxStackSize();
		for (int i = 0; i < 36; i++) {
			ItemStack a = player.getInventory().getItem(i);
			if (a == null || a.getType() == Material.AIR) {
				cnt += maxStack;
				continue;
			}
			if (SerializedItem.of(a).areEqualIgnoreAmount(si))
				cnt += a.getMaxStackSize() - a.getAmount();
		}
		return cnt;
	}

	public static int addItemStack(Player player, ItemStack is, int amount) {
		return addItemStack(player, SerializedItem.of(is), amount);
	}

	public static int addItemStack(Player player, SerializedItem si, int amount) {
		for (int s = 0; s < 36; s++) {
			if (amount == 0)
				return amount;
			ItemStack a = player.getInventory().getItem(s);
			if (a == null || a.getType() == Material.AIR) {
				ItemStack clone = si.toItem();
				clone.setAmount(Math.min(amount, clone.getMaxStackSize()));
				amount -= clone.getAmount();
				player.getInventory().setItem(s, clone);
			} else {
				if (!SerializedItem.of(a).areEqualIgnoreAmount(si))
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
		return removeItemStack(player, SerializedItem.of(is), amount);
	}

	/**
	 * 
	 * @param player
	 * @param si
	 * @param amount
	 * @return how many not removed
	 */
	public static int removeItemStack(Player player, SerializedItem si, int amount) {
		ItemStack[] content = player.getInventory().getContents();
		for (int s = 0; s < content.length; s++) {
			if (amount == 0)
				return amount;
			ItemStack a = content[s];
			if (a == null || a.getType() == Material.AIR)
				continue;
			if (!SerializedItem.of(a).areEqualIgnoreAmount(si))
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
