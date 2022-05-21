package de.ancash.minecraft.crafting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ContainerWorkbench_1_8_1_13 extends IContainerWorkbench{
	
	private static Constructor<?> containerWorkbenchConstructor;
	private static Constructor<?> blockPositionConstructor;
	private static Field playerInventoryField;
	private static Field worldField;
	private static Field inventoryCraftingField;
	private static Field getCurrentIRecipeField;
	
	private static Method setItemMethod;
	private static Method getItemMethod;
	private static Random r;
	
	static void initReflection() throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		r = new Random();
		Class<?> craftPlayer = getCraftBukkitClass("entity.CraftPlayer");
		playerToEntityHumanMethod = craftPlayer.getMethod("getHandle");
		
		iRecipeToBukkitRecipeMethod = getNMSClass("IRecipe").getDeclaredMethod("toBukkitRecipe");
		itemStackAsNMSCopyMethod = getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
		nmsItemStackAsBukkitCopy = getCraftBukkitClass("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", getNMSClass("ItemStack"));
		setItemMethod = getNMSClass("InventoryCrafting").getDeclaredMethod("setItem", int.class, getNMSClass("ItemStack"));
		getItemMethod = getNMSClass("InventoryCrafting").getDeclaredMethod("getItem", int.class);
		
		Class<?> entityHuman = getNMSClass("EntityHuman");
		playerInventoryField = entityHuman.getDeclaredField("inventory");
		playerInventoryField.setAccessible(true);

		worldField = getNMSClass("Entity").getDeclaredField("world");
		worldField.setAccessible(true);
		
		Class<?> containerWorkbench = getNMSClass("ContainerWorkbench");
		containerWorkbenchConstructor = containerWorkbench.getConstructor(
				getNMSClass("PlayerInventory"),
				getNMSClass("World"),
				getNMSClass("BlockPosition"));
		inventoryCraftingField = containerWorkbench.getDeclaredField("craftInventory");
		inventoryCraftingField.setAccessible(true);
		blockPositionConstructor = getNMSClass("BlockPosition").getDeclaredConstructor(int.class, int.class, int.class);
		
		getCurrentIRecipeField = getNMSClass("InventoryCrafting").getDeclaredField("currentRecipe");
		getCurrentIRecipeField.setAccessible(true);
	}

	private static Object newBlockPosition() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return blockPositionConstructor.newInstance(r.nextInt(10000) - 5000, r.nextInt(255), r.nextInt(10000) - 5000);
	}
	
	private final Player player;
	
	private final Object containerWorkbench;
	private final Object inventoryCrafting;
	
	ContainerWorkbench_1_8_1_13(Player player) {
		try {
			
			this.player = player;
			Object entityHuman = playerToEntityHuman(player);
			this.containerWorkbench = containerWorkbenchConstructor.newInstance(
					getPlayerInventory(entityHuman), 
					getWorld(entityHuman),
					newBlockPosition());
			this.inventoryCrafting = getInventoryCrafting(containerWorkbench);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
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
	
	private static Object getInventoryCrafting(Object containerWorkbench) throws IllegalArgumentException, IllegalAccessException {
		return inventoryCraftingField.get(containerWorkbench);
	}
	
	private static Object getPlayerInventory(Object entityHuman) {
		try {
			return playerInventoryField.get(entityHuman);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getWorld(Object entityHuman) {
		try {
			return worldField.get(entityHuman);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
