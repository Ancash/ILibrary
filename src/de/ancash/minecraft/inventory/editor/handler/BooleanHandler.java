package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.Bukkit;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public class BooleanHandler implements IValueHandler<Boolean> {

	public static final BooleanHandler INSTANCE = new BooleanHandler();

	BooleanHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isBoolean(key);
	}

	@Override
	public Boolean get(ConfigurationSection section, String s) {
		return section.getBoolean(s);
	}

	@Override
	public void set(ConfigurationSection section, String key, Boolean value) {
		section.set(key, value);
	}

	@Override
	public Class<Boolean> getClazz() {
		return Boolean.class;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		editor.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new BooleanEditor(editor, key), 1);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return String.valueOf(get(section, s));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Boolean;
	}

}
