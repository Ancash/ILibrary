package de.ancash.minecraft.inventory.editor.handler;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public class ConfigurationSectionHandler implements IValueHandler<ConfigurationSection> {

	public static final ConfigurationSectionHandler INSTANCE = new ConfigurationSectionHandler();

	ConfigurationSectionHandler() {
	}

	@Override
	public ConfigurationSection get(ConfigurationSection section, String s) {
		return section.getConfigurationSection(s);
	}

	@Override
	public void set(ConfigurationSection section, String key, ConfigurationSection value) {
		section.createSection(key, value.getValues(true));
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isConfigurationSection(key);
	}

	@Override
	public Class<ConfigurationSection> getClazz() {
		return ConfigurationSection.class;
	}

	@SuppressWarnings("nls")
	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return get(section, s).getKeys(false).toString().replaceAll("(.{1,50})\\s+", "$1\n");
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		editor.setCurrentConfigurationSection(get(editor.getCurrentConfigurationSection(), key));
		editor.newInventory();
		editor.open();
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof ConfigurationSection;
	}
}
