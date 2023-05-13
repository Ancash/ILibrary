package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.yml.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

public interface IValueHandler<T> {

	public boolean isValid(ConfigurationSection section, String key);

	public boolean isValid(Object o);

	public T get(ConfigurationSection section, String s);

	public void set(ConfigurationSection section, String key, T value);

	public Class<?> getClazz();

	public default ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		return ItemStackUtils.setDisplayname(editor.getSettings().getKeyValueItem(editor.getCurrent(), key, this),
				ChatColor.WHITE.toString() + key);
	}

	public void edit(ConfigurationSectionEditor editor, String key);

	public String valueToString(ConfigurationSection section, String s);

	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<T> valSup, Consumer<T> onEdit, Runnable onBack, Runnable onDelete);

	public T defaultValue();

	@SuppressWarnings("unchecked")
	public default void uncheckedEdit(YamlEditor yfe, ValueEditor<?> parent, String key,
			List<IValueHandler<?>> valHandler, UUID id, String title, Supplier<?> valSup, Consumer<?> onEdit,
			Runnable onBack, Runnable onDelete) {
		edit(yfe, parent, key, valHandler, id, title, (Supplier<T>) valSup, (Consumer<T>) onEdit, onBack, onDelete);
	}

	public ItemStack getAddItem();
}
