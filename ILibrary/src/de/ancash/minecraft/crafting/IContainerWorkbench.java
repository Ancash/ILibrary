package de.ancash.minecraft.crafting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import de.ancash.minecraft.IItemStack;
import de.ancash.minecraft.nbt.utils.MinecraftVersion;

public abstract class IContainerWorkbench {

	static final ConcurrentHashMap<List<Integer>, Optional<Recipe>> cache = new ConcurrentHashMap<>();

	static final String VERSION = MinecraftVersion.getVersion().getPackageName();
	static Method playerToEntityHumanMethod;
	static Method iRecipeToBukkitRecipeMethod;
	static Method itemStackAsNMSCopyMethod;
	static Method nmsItemStackAsBukkitCopy;

	static {
		try {
			itemStackAsNMSCopyMethod = getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy",
					ItemStack.class);
			playerToEntityHumanMethod = getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
			if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1)) {
				iRecipeToBukkitRecipeMethod = getNMSClass("IRecipe").getDeclaredMethod("toBukkitRecipe");
				nmsItemStackAsBukkitCopy = getCraftBukkitClass("inventory.CraftItemStack")
						.getDeclaredMethod("asBukkitCopy", getNMSClass("ItemStack"));
			} else {
				nmsItemStackAsBukkitCopy = getCraftBukkitClass("inventory.CraftItemStack")
						.getDeclaredMethod("asBukkitCopy", Class.forName("net.minecraft.world.item.ItemStack"));
				iRecipeToBukkitRecipeMethod = Class.forName("net.minecraft.world.item.crafting.IRecipe")
						.getDeclaredMethod("toBukkitRecipe");
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	static Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
	}

	static Class<?> getNMSClass(String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + VERSION + "." + name);
	}

	static Object playerToEntityHuman(Player player) {
		try {
			return playerToEntityHumanMethod.invoke(player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Recipe getRecipe(ItemStack[] ings) {
		return getRecipe(Stream.of(ings).map(i -> i != null ? new IItemStack(i) : null).toArray(IItemStack[]::new),
				Stream.of(ings).map(i -> i != null ? i.hashCode() : null).collect(Collectors.toList()));
	}

	public Recipe getRecipe(IItemStack[] ings, List<Integer> key) {
		return getRecipe(Stream.of(ings).map(i -> i != null ? i.getOriginal() : null).toArray(ItemStack[]::new), key);
	}

	public Recipe getRecipe(IItemStack[] ings) {
		return getRecipe(ings, Stream.of(ings).map(i -> i != null ? i.hashCode() : null).collect(Collectors.toList()));
	}

	public Recipe getRecipe(ItemStack[] ings, List<Integer> key) {
		if (!cache.containsKey(key)) {
			for (int i = 0; i < 9; i++)
				setItem(i, ings[i]);
			Recipe r = getCurrentRecipe();
			for (int i = 0; i < 9; i++)
				setItem(i, null);
			cache.put(key, Optional.ofNullable(r));
		}
		return cache.get(key).orElse(null);
	}

	public abstract Player getPlayer();

	public abstract Recipe getCurrentRecipe();

	public abstract void setItem(int i, ItemStack item);

	public abstract ItemStack getItem(int i);
}