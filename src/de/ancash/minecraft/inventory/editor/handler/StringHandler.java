package de.ancash.minecraft.inventory.editor.handler;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.StringEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

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
		edit(editor.getFile(), editor.getValueHandler(), editor.getId(),
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrent(), key,
						editor.getHandler(key).getClazz(), 32),
				() -> editor.getCurrent().getString(key), s -> editor.getCurrent().set(key, s), editor::open,
				() -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof String || o instanceof Number || o instanceof Boolean;
	}

	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<String> valSup, Consumer<String> onEdit, Runnable onBack, Runnable onDelete) {
		StringEditor se = new StringEditor(id, title, yfe.getSettings(), valSup, onEdit, onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> se.open(), 1);
	}

	@SuppressWarnings("nls")
	@Override
	public String defaultValue() {
		return "";
	}
}
