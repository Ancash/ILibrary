package de.ancash.minecraft.inventory.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class CompositeModuleRegistry {

	private static final Map<String, CompositeModuleSupplier> map = new HashMap<>();

	public static void register(JavaPlugin plugin, CompositeModuleSupplier parser) {
		map.put(plugin.getName().toLowerCase(), parser);
	}

	@SuppressWarnings("nls")
	public static CompositeModule parse(String id, List<Integer> slots, Map<String, Object> props) {
		System.out.println("parse");
		if (!map.containsKey(id.split("_")[0].toLowerCase())) {
			System.out.println("no parser for " + id + " found slots=" + slots + " props=" + props);
			return null;
		}
		return map.get(id.split("_")[0].toLowerCase()).parseModule(id, slots, props);
	}
}
