package de.ancash.minecraft.crafting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ContainerWorkbench_MC1_21_R4 extends IContainerWorkbench {

	private static Constructor<?> containerWorkbenchConstructor;
	private static Constructor<?> blockPositionConstructor;
	private static Field playerInventoryField;
	private static Method containerAccessMethod;
	private static Field worldField;

	private static Field craftingContainerField;
	private static Field recipeHolderField;
	private static Method toBukkitRecipe;
	private static Field currentIRecipe;
	private static Method setItemMethod;
	private static Method getItemMethod;

	@SuppressWarnings("nls")
	static void initReflection() throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException {
		Class<?> bpc = Class.forName("net.minecraft.core.BlockPos");
		Class<?> containerLevelAccess = Class.forName("net.minecraft.world.inventory.ContainerLevelAccess");
		Class<?> containerWorkbench = Class.forName("net.minecraft.world.inventory.CraftingMenu");
		Class<?> transientCraftingContainer = Class.forName("net.minecraft.world.inventory.TransientCraftingContainer");
		Class<?> iic = Class.forName("net.minecraft.world.Container");
		Class<?> abstractCraftingMenu = Class.forName("net.minecraft.world.inventory.AbstractCraftingMenu");
		blockPositionConstructor = bpc.getDeclaredConstructor(int.class, int.class, int.class);
		containerWorkbenchConstructor = containerWorkbench.getDeclaredConstructor(int.class, Class.forName("net.minecraft.world.entity.player.PlayerInventory"),
				containerLevelAccess);
		containerAccessMethod = containerLevelAccess.getDeclaredMethod("create", Class.forName("net.minecraft.world.level.Level"), bpc);
		Class<?> entityHuman = Class.forName("net.minecraft.world.entity.player.Player");
		playerInventoryField = entityHuman.getDeclaredField("ct");
		playerInventoryField.setAccessible(true);
		worldField = Class.forName("net.minecraft.world.entity.Entity").getDeclaredField("az");
		worldField.setAccessible(true);

		setItemMethod = iic.getDeclaredMethod("setItem", int.class, Class.forName("net.minecraft.world.item.ItemStack"));
		getItemMethod = iic.getDeclaredMethod("getItem", int.class);

		craftingContainerField = abstractCraftingMenu.getDeclaredField("craftSlots");

		recipeHolderField = transientCraftingContainer.getDeclaredField("currentRecipe");
		recipeHolderField.setAccessible(true);
		Class<?> recipeHolder = Class.forName("net.minecraft.world.item.crafting.RecipeHolder");
		toBukkitRecipe = recipeHolder.getDeclaredMethod("toBukkitRecipe");
		currentIRecipe = recipeHolder.getDeclaredField("c");
		currentIRecipe.setAccessible(true);
	}

	private static Object newBlockPosition()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return blockPositionConstructor.newInstance(0, 0, 0);
	}

	private final Player player;
	private final Object inventoryCrafting;

	ContainerWorkbench_MC1_21_R4(Player player) throws ClassNotFoundException {
		try {
			this.player = player;
			Object nmsPlayer = playerToEntityHuman(player);
			Object containerAccess = containerAccessMethod.invoke(player, worldField.get(nmsPlayer), newBlockPosition());
			Object containerWorkbench = containerWorkbenchConstructor.newInstance(0, playerInventoryField.get(playerToEntityHuman(player)),
					containerAccess);
			inventoryCrafting = craftingContainerField.get(containerWorkbench);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	protected Object getInventoryCrafting() {
		return inventoryCrafting;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Recipe getCurrentRecipe() {
		try {
			Object ir = getCurrentRecipeHolder();
			if (ir == null)
				return null;
			return (Recipe) toBukkitRecipe.invoke(ir);
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
			// e.printStackTrace();
		}
	}

	public Object getCurrentRecipeHolder() {
		try {
			return recipeHolderField.get(inventoryCrafting);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getCurrentIRecipe() {
		try {
			return currentIRecipe.get(getCurrentRecipeHolder());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}