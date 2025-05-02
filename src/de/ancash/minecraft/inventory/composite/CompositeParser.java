package de.ancash.minecraft.inventory.composite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation;

import de.ancash.misc.ReflectionUtils;

@SuppressWarnings("nls")
public class CompositeParser {

	public static void main(String[] args) throws IOException {
		YamlFile file = new YamlFile(new SnakeYamlImplementation());
		ConfigurationSection cs = file.createSection("test");
		cs.set(INV_SIZE, 54);
		cs.set(INV_TITLE, "title");
		List<Map<String, Object>> mods = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> m = new HashMap<>();
			m.put(ID, "bazaar:" + i);
			m.put(SLOTS, IntStream.range(0, i).boxed().collect(Collectors.toList()));
			Map<String, Object> props = new HashMap<>();
			props.put("iter", i);
			m.put(PROPERTIES, props);
			mods.add(m);
		}
		cs.set(MODULES, mods);
		System.out.println(file.saveToString());
		System.out.println(ReflectionUtils.toString(parseGUI(file, "test")));
	}

	public static final String MODULES = "modules";
	public static final String INV_SIZE = "size";
	public static final String INV_TITLE = "title";

	public static final String SLOTS = "slots";
	public static final String ID = "id";
	public static final String PROPERTIES = "props";

	CompositeParser() {

	}

	@SuppressWarnings("unchecked")
	public static CompositeModuleSupplier parseModule(Map<String, Object> map) {
		return CompositeModuleRegistry.parse(getString(map, ID), getSlots(map),
				(Map<String, Object>) map.getOrDefault(PROPERTIES, Collections.emptyMap()));
	}

	@SuppressWarnings({ "unchecked" })
	public static CompositeGUISupplier parseGUI(YamlFile file, String path) {
		String title;
		int size;
		List<CompositeModuleSupplier> modules = new ArrayList<>();
		if (path.isEmpty() || path.equals(".")) {
			title = file.getString(INV_TITLE);
			size = file.getInt(INV_SIZE);
			if (file.contains(MODULES))
				modules = ((List<Map<String, Object>>) file.getList(MODULES)).stream().map(CompositeParser::parseModule).filter(m -> m != null)
						.collect(Collectors.toList());
		} else {
			title = file.getString(String.join(".", path, INV_TITLE));
			size = file.getInt(String.join(".", path, INV_SIZE));
			if (file.contains(String.join(".", path, MODULES)))
				modules = ((List<Map<String, Object>>) file.getList(String.join(".", path, MODULES))).stream().map(CompositeParser::parseModule)
						.filter(m -> m != null).collect(Collectors.toList());
		}

		if (size % 9 != 0 || size < 0 || size > 9 * 6)
			throw new IllegalArgumentException("invalid size " + size);

		return new CompositeGUISupplier(title, size, modules);
	}

	private static String getString(Map<String, Object> map, String key) {
		if (!map.containsKey(key) || map.get(key) == null || !(map.get(key) instanceof String))
			throw new IllegalArgumentException("invalid string " + key + ":" + map.get(key));
		return (String) map.get(key);
	}

	@SuppressWarnings("unchecked")
	private static List<Integer> getSlots(Map<String, Object> map) {
		List<Integer> slots = (List<Integer>) map.get(SLOTS);
		if (slots == null)
			throw new IllegalArgumentException("slots null in " + map);
		for (int slot : slots)
			if (slot < 0 || slot >= 9 * 6)
				throw new IllegalArgumentException("slot out of range " + slot);
		return slots;
	}
}
