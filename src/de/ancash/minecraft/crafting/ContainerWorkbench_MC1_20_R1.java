package de.ancash.minecraft.crafting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ContainerWorkbench_MC1_20_R1 extends IContainerWorkbench {

	private static Constructor<?> containerWorkbenchConstructor;
	private static Constructor<?> blockPositionConstructor;
	private static Field playerInventoryField;
	private static Method containerAccessMethod;
	private static Field worldField;

	private static Field inventoryCraftingField;
	private static Field getCurrentIRecipeField;
	private static Method setItemMethod;
	private static Method getItemMethod;

	@SuppressWarnings("nls")
	static void initReflection() throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException {
		Class<?> bpc = Class.forName("net.minecraft.core.BlockPosition");
		Class<?> cac = Class.forName("net.minecraft.world.inventory.ContainerAccess");
		Class<?> cwc = Class.forName("net.minecraft.world.inventory.ContainerWorkbench");
		Class<?> tcc = Class.forName("net.minecraft.world.inventory.TransientCraftingContainer");
		Class<?> iic = Class.forName("net.minecraft.world.IInventory");
		blockPositionConstructor = bpc.getDeclaredConstructor(int.class, int.class, int.class);
		containerWorkbenchConstructor = cwc.getDeclaredConstructor(int.class, Class.forName("net.minecraft.world.entity.player.PlayerInventory"),
				cac);
		containerAccessMethod = cac.getDeclaredMethod("a", Class.forName("net.minecraft.world.level.World"), bpc);
		Class<?> entityHuman = Class.forName("net.minecraft.world.entity.player.EntityHuman");
		playerInventoryField = entityHuman.getDeclaredField("cl");
		playerInventoryField.setAccessible(true);
		worldField = Class.forName("net.minecraft.world.entity.Entity").getDeclaredField("t");
		worldField.setAccessible(true);

		setItemMethod = iic.getDeclaredMethod("a", int.class, Class.forName("net.minecraft.world.item.ItemStack"));
		getItemMethod = iic.getDeclaredMethod("a", int.class);

		inventoryCraftingField = ReflectionUtil.findField(cwc, tcc);

		getCurrentIRecipeField = tcc.getDeclaredField("currentRecipe");
		getCurrentIRecipeField.setAccessible(true);
	}

	private static Object newBlockPosition()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return blockPositionConstructor.newInstance(0, 0, 0);
	}

	private final Player player;
	private final Object inventoryCrafting;

	ContainerWorkbench_MC1_20_R1(Player player) throws ClassNotFoundException {
		try {
			this.player = player;
			Object nmsPlayer = playerToEntityHuman(player);
			Object containerAccess = containerAccessMethod.invoke(player, worldField.get(nmsPlayer), newBlockPosition());
			Object containerWorkbench = containerWorkbenchConstructor.newInstance(0, playerInventoryField.get(playerToEntityHuman(player)),
					containerAccess);
			inventoryCrafting = inventoryCraftingField.get(containerWorkbench);
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
			Object ir = getCurrentIRecipe();
			if (ir == null)
				return null;
			return (Recipe) iRecipeToBukkitRecipeMethod.invoke(ir);
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

	@Override
	public Object getCurrentIRecipe() {
		try {
			return getCurrentIRecipeField.get(inventoryCrafting);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}