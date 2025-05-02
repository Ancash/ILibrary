package de.ancash.minecraft.inventory.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class CompositeModuleRegistry {

	private static final Map<String, CompositeModuleParser> map = new HashMap<>();

	public static void register(JavaPlugin plugin, CompositeModuleParser parser) {
		map.put(plugin.getName().toLowerCase(), parser);
	}

	@SuppressWarnings("nls")
	public static CompositeModuleSupplier parse(String id, List<Integer> slots, Map<String, Object> props) {
		if (id == null || id.isEmpty())
			throw new IllegalArgumentException("id null");
		if (!id.contains(":"))
			throw new IllegalArgumentException("id invalid: " + id);
		String pl = id.split(":")[0].toLowerCase();
		if (!map.containsKey(pl))
			throw new IllegalArgumentException("no parser for " + id + " found slots=" + slots + " props=" + props);
		String val = id.substring(pl.length() + 1);
		if (val == null || val.isEmpty())
			throw new IllegalArgumentException("value invalid: " + id);
		return map.get(pl).parseModule(val, slots, props);
	}
}
