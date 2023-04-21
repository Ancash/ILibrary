package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public class DoubleHandler implements IValueHandler<Double> {

	public static final DoubleHandler INSTANCE = new DoubleHandler();

	DoubleHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isDouble(key);
	}

	@Override
	public void set(ConfigurationSection section, String key, Double value) {
		section.set(key, value);
	}

	@Override
	public Class<Double> getClazz() {
		return Double.class;
	}

	@Override
	public Double get(ConfigurationSection section, String s) {
		return section.getDouble(s);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return String.valueOf(get(section, s));
	}

	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.LADDER.parseMaterial());
		return item;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		editor.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new DoubleEditor(editor, key), 1);
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Double || o instanceof Float;
	}
}
