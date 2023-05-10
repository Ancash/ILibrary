package de.ancash.minecraft.inventory.editor.handler;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.MemoryConfiguration;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

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
		edit(editor.getFile(), editor.getValueHandler(), editor.getId(),
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrent().getConfigurationSection(key), 32),
				() -> editor.getCurrent().getConfigurationSection(key), k -> {
					throw new UnsupportedOperationException();
				}, () -> editor.open(), () -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof ConfigurationSection;
	}

	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<ConfigurationSection> valSup, Consumer<ConfigurationSection> onEdit, Runnable onBack,
			Runnable onDelete) {
		ConfigurationSectionEditor e = new ConfigurationSectionEditor(yfe, Bukkit.getPlayer(id), yfe.getRoot(),
				valSup.get(), onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> {
			e.addRootBackItem(onBack);
			e.open();
		}, 1);
	}

	@Override
	public ConfigurationSection defaultValue() {
		return new MemoryConfiguration();
	}
}
