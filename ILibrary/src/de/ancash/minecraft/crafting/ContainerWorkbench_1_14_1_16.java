package de.ancash.minecraft.crafting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ContainerWorkbench_1_14_1_16 extends IContainerWorkbench{
	
	private static Constructor<?> containerWorkbenchConstructor;
	private static Constructor<?> blockPositionConstructor;
	private static Field playerInventoryField;
	private static Method containerAccessMethod;
	private static Field worldField;
	
	private static Field inventoryCraftingField;
	private static Field getCurrentIRecipeField;
	private static Method setItemMethod;
	private static Method getItemMethod;
	private static Random r;
	
	static void initReflection() throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {
		r = new Random();
		blockPositionConstructor = getNMSClass("BlockPosition").getDeclaredConstructor(int.class, int.class, int.class);
		containerWorkbenchConstructor = getNMSClass("ContainerWorkbench").getDeclaredConstructor(
				int.class,
				getNMSClass("PlayerInventory"),
				getNMSClass("ContainerAccess")
				);
		containerAccessMethod = getNMSClass("ContainerAccess").getDeclaredMethod("at", getNMSClass("World"), getNMSClass("BlockPosition"));
		Class<?> entityHuman = getNMSClass("EntityHuman");
		playerInventoryField = entityHuman.getDeclaredField("inventory");
		playerInventoryField.setAccessible(true);
		worldField = getNMSClass("Entity").getDeclaredField("world");
		worldField.setAccessible(true);
		
		setItemMethod = getNMSClass("InventoryCrafting").getDeclaredMethod("setItem", int.class, getNMSClass("ItemStack"));
		getItemMethod = getNMSClass("InventoryCrafting").getDeclaredMethod("getItem", int.class);
		
		

		Class<?> containerWorkbench = getNMSClass("ContainerWorkbench");
		inventoryCraftingField = containerWorkbench.getDeclaredField("craftInventory");
		inventoryCraftingField.setAccessible(true);
		
		getCurrentIRecipeField = getNMSClass("InventoryCrafting").getDeclaredField("currentRecipe");
		getCurrentIRecipeField.setAccessible(true);
	}
	
	private static Object newBlockPosition() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return blockPositionConstructor.newInstance(r.nextInt(10000) - 5000, r.nextInt(255), r.nextInt(10000) - 5000);
	}
	
	private final Player player;
	private final Object inventoryCrafting;
	
	ContainerWorkbench_1_14_1_16(Player player) throws ClassNotFoundException {
		try {
			this.player = player;
			Object nmsPlayer = playerToEntityHuman(player);
			Object containerAccess = containerAccessMethod.invoke(player, worldField.get(nmsPlayer), newBlockPosition());
			Object containerWorkbench = containerWorkbenchConstructor.newInstance(0, playerInventoryField.get(playerToEntityHuman(player)), containerAccess);
			inventoryCrafting = inventoryCraftingField.get(containerWorkbench);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Recipe getCurrentRecipe() {
		try {
			Object ir = getCurrentIRecipe();
			if(ir == null) return null;
	 		return (Recipe) iRecipeToBukkitRecipeMethod.invoke(getCurrentIRecipe());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public ItemStack getItem(int i) {
		try {
			return (ItemStack) nmsItemStackAsBukkitCopy.invoke(null, getItemMethod.invoke(inventoryCrafting, i));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void setItem(int i, ItemStack item) {
		try {
			setItemMethod.invoke(inventoryCrafting, i, itemStackAsNMSCopyMethod.invoke(null, item));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//e.printStackTrace();
		}
	}
	
	public Object getCurrentIRecipe() throws IllegalArgumentException, IllegalAccessException {
		return getCurrentIRecipeField.get(inventoryCrafting);
	}
}