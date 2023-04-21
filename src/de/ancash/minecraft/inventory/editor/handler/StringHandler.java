package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public class StringHandler implements IValueHandler<String> {

	public static final StringHandler INSTANCE = new StringHandler();

	StringHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isString(key);
	}

	@Override
	public void set(ConfigurationSection section, String key, String value) {
		section.set(key, value);
	}

	@Override
	public Class<String> getClazz() {
		return String.class;
	}

	@Override
	public String get(ConfigurationSection section, String s) {
		return section.getString(s);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return ItemStackUtils.translateChatColor(get(section, s), '&');
	}

	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.OAK_SIGN.parseMaterial());
		return item;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		editor.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new StringEditor(editor, key), 1);
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof String || o instanceof Number || o instanceof Boolean;
	}
}
