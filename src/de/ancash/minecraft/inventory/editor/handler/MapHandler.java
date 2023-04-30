package de.ancash.minecraft.inventory.editor.handler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.MemoryConfiguration;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

@SuppressWarnings("rawtypes")
public class MapHandler implements IValueHandler<Map> {

	public static final MapHandler INSTANCE = new MapHandler();

	MapHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return false;
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Map;
	}

	@Override
	public Map<String, Object> get(ConfigurationSection section, String s) {
		return section.getConfigurationSection(s).getMapValues(true);
	}

	@Override
	public void set(ConfigurationSection section, String key, Map value) {
		section.createSection(key, value);
	}

	@Override
	public Class<Map> getClazz() {
		return Map.class;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return ConfigurationSectionHandler.INSTANCE.valueToString(section, s);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<Map> valSup, Consumer<Map> onEdit, Runnable onBack) {
		MemoryConfiguration mc = new MemoryConfiguration();
		Map<String, Object> m = valSup.get();
		m.entrySet().forEach(entry -> mc.set(entry.getKey(), entry.getValue()));
		ConfigurationSectionHandler.INSTANCE.edit(yfe, valHandler, id, title, () -> mc, null, () -> {
			onEdit.accept(mc.getMapValues(true));
			onBack.run();
		});
	}

	@Override
	public Map defaultValue() {
		return new LinkedHashMap<>();
	}
}
