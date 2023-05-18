package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.MemoryConfiguration;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

@SuppressWarnings("rawtypes")
public class MapHandler implements IValueHandler<Map> {

	public static final MapHandler INSTANCE = new MapHandler();

	protected MapHandler() {
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
	public ItemStack getAddItem() {
		return null;
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
	public Class<?> getClazz() {
		return ConfigurationSection.class;
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
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<Map> valSup, Consumer<Map> onEdit, Runnable onBack, Runnable onDelete) {
		MemoryConfiguration mc = new MemoryConfiguration();
		Map<String, Object> m = valSup.get();
		m.entrySet().forEach(entry -> mc.set(entry.getKey(), entry.getValue()));
		ConfigurationSectionHandler.INSTANCE.edit(yfe, parent, key, valHandler, id, title, () -> mc, null, () -> {
			onEdit.accept(mc.getMapValues(true));
			onBack.run();
		}, onDelete);
	}

	@Override
	public Map defaultValue() {
		return new LinkedHashMap<>();
	}
}
