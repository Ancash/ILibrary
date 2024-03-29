package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.MemoryConfiguration;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public class ConfigurationSectionHandler implements IValueHandler<ConfigurationSection> {

	public static final ConfigurationSectionHandler INSTANCE = new ConfigurationSectionHandler();

	protected ConfigurationSectionHandler() {
	}

	@Override
	public ConfigurationSection get(ConfigurationSection section, String s) {
		if (!isValid(section, s))
			throw new IllegalStateException(String.join(".", section.getCurrentPath(), s) + " is not cs but: " + section.get(s).getClass());
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addDefaultToList(ValueEditor<?> where, List list, int pos) {
		ConfigurationSectionEditor cse = where.getClosesConfigurationSectionEditor();
		if (cse != null) {
			list.add(pos, new CustomMemoryConfiguration(cse.getCurrent()));
		} else {
			list.add(pos, defaultValue());
		}
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.CHEST).setDisplayname("§7Add ConfigurationSection").build();
	}

	@Override
	public Class<?> getClazz() {
		return ConfigurationSection.class;
	}

	@SuppressWarnings("nls")
	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return get(section, s).getKeys(false).toString().replaceAll("(.{1,50})\\s+", "$1\n");
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		edit(editor.getYamlEditor(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent().getConfigurationSection(key), 32),
				() -> editor.getCurrent().getConfigurationSection(key), k -> {
					throw new UnsupportedOperationException();
				}, () -> editor.open(), () -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof ConfigurationSection;
	}

	@Override
	public void setDefaultValue(ConfigurationSection section, String key) {
		section.createSection(key);
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<ConfigurationSection> valSup, Consumer<ConfigurationSection> onEdit, Runnable onBack, Runnable onDelete) {
		ConfigurationSectionEditor e = new ConfigurationSectionEditor(yfe, parent, key, Bukkit.getPlayer(id), valSup.get(), onDelete);
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
