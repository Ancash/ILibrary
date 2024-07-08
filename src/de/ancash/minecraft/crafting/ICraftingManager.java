package de.ancash.minecraft.crafting;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import de.ancash.ILibrary;
import de.tr7zw.nbtapi.utils.MinecraftVersion;

public class ICraftingManager {

	private static ICraftingManager singleton = new ICraftingManager();

	public static ICraftingManager getSingleton() {
		return singleton;
	}

	private Class<? extends IContainerWorkbench> clazz;

	private ICraftingManager() {

	}

	@SuppressWarnings("nls")
	public void init(ILibrary il) {
		il.getLogger().info("Init version specific " + getClass().getSimpleName() + " for " + MinecraftVersion.getVersion().name());
		try {
			if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1)) {
				ContainerWorkbench_1_8_1_13.initReflection();
				clazz = ContainerWorkbench_1_8_1_13.class;
			} else if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1)) {
				ContainerWorkbench_1_14_1_16.initReflection();
				clazz = ContainerWorkbench_1_14_1_16.class;
			} else if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1)) {
				ContainerWorkbench_1_17.initReflection();
				clazz = ContainerWorkbench_1_17.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_18_R1)) {
				ContainerWorkbench_1_18_R1.initReflection();
				clazz = ContainerWorkbench_1_18_R1.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_18_R2)) {
				ContainerWorkbench_1_18_R2.initReflection();
				clazz = ContainerWorkbench_1_18_R2.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_19_R1)) {
				ContainerWorkbench_1_19_R1.initReflection();
				clazz = ContainerWorkbench_1_19_R1.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_19_R2)) {
				ContainerWorkbench_1_19_R2.initReflection();
				clazz = ContainerWorkbench_1_19_R2.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_19_R3)) {
				ContainerWorkbench_1_19_R3.initReflection();
				clazz = ContainerWorkbench_1_19_R3.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_20_R1)) {
				ContainerWorkbench_1_20_R1.initReflection();
				clazz = ContainerWorkbench_1_20_R1.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_20_R2)) {
				ContainerWorkbench_1_20_R2.initReflection();
				clazz = ContainerWorkbench_1_20_R2.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_20_R3)) {
				ContainerWorkbench_1_20_R3.initReflection();
				clazz = ContainerWorkbench_1_20_R3.class;
			} else if (MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_20_R4)) {
				ContainerWorkbench_1_20_R4.initReflection();
				clazz = ContainerWorkbench_1_20_R4.class;
			} else if(MinecraftVersion.getVersion().equals(MinecraftVersion.MC1_21_R1)) {
				ContainerWorkbench_1_21_R1.initReflection();
				clazz = ContainerWorkbench_1_21_R1.class;
			} else {
				throw new IllegalStateException("unsupported version " + MinecraftVersion.getVersion());
			}
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalArgumentException
				| IllegalAccessException e) {
			il.getLogger().severe("Reflection failed:");
			e.printStackTrace();
		}

		il.getLogger().info("Using: " + clazz.getTypeName());
	}

	public IContainerWorkbench newInstance(Player player) {
		try {
			return clazz.getDeclaredConstructor(Player.class).newInstance(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}