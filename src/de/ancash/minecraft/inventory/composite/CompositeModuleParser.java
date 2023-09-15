package de.ancash.minecraft.inventory.composite;

import java.util.Map;

public class CompositeModuleParser {

	public static final String SLOT = "slot";
	public static final String ID = "id";
	public static final String PROPERTIES = "properties";

	CompositeModuleParser() {

	}

	public static CompositeModule parse(Map<String, Object> map) {
		int slot = getSlot(map);
		String id = getString(map, ID);
		return null;
	}

	private static String getString(Map<String, Object> map, String key) {
		if (!map.containsKey(key) || map.get(key) == null || !(map.get(key) instanceof String))
			throw new IllegalArgumentException("invalid string " + key + ":" + map.get(key));
		return (String) map.get(key);
	}

	private static long getLong(Map<String, Object> map, String key) {
		if (!map.containsKey(key) || map.get(key) == null || !(map.get(key) instanceof Long
				|| map.get(key) instanceof Integer || map.get(key) instanceof Byte || map.get(key) instanceof Short))
			throw new IllegalArgumentException("invalid long " + key + ":" + map.get(key));
		return ((Number) map.get(key)).longValue();
	}

	private static int getSlot(Map<String, Object> map) {
		int slot = (int) getLong(map, SLOT);
		if (slot < 0 || slot >= 9 * 6)
			throw new IllegalArgumentException("slot out of range " + slot);
		return slot;
	}
}
