package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.StringEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public class StringHandler implements IValueHandler<String> {

	public static final StringHandler INSTANCE = new StringHandler();

	protected StringHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isString(key);
	}

	@Override
	public Class<?> getClazz() {
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
		edit(editor.getYamlEditor(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(), 32),
				() -> editor.getCurrent().getString(key), s -> editor.getCurrent().set(key, s), editor::open, () -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof String || o instanceof Number || o instanceof Boolean;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<String> valSup, Consumer<String> onEdit, Runnable onBack, Runnable onDelete) {
		StringEditor se = new StringEditor(id, title, parent, yfe, key, valSup, onEdit, onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> se.open(), 1);
	}

	@SuppressWarnings("nls")
	@Override
	public String defaultValue() {
		return "";
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname("§7Add String").build();
	}
}
