package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public interface IValueHandler<T> {

	public boolean isValid(ConfigurationSection section, String key);

	public boolean isValid(Object o);

	public T get(ConfigurationSection section, String s);

	public void set(ConfigurationSection section, String key, T value);

	public Class<T> getClazz();

	public default ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		return ItemStackUtils.setDisplayname(
				editor.getSettings().getKeyValueItem(editor.getCurrentConfigurationSection(), key, this),
				ChatColor.WHITE.toString() + key);
	}

	public void edit(ConfigurationSectionEditor editor, String key);

	public String valueToString(ConfigurationSection section, String s);
}
