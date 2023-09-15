package de.ancash.minecraft.inventory.composite;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class CompositeRegistry {

	private static final Map<String, Object> map = new HashMap<>();

	@SuppressWarnings("nls")
	public static void register(JavaPlugin plugin, String id) {
		map.put(String.join(":", plugin.getName().toLowerCase(), id), 1);
	}

}
