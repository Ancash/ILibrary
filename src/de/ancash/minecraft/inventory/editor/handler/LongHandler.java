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
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.EditorSettings;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

public class LongHandler implements IValueHandler<Long> {

	public static final LongHandler INSTANCE = new LongHandler();

	LongHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isLong(key) || section.isInt(key);
	}

	@Override
	public void set(ConfigurationSection section, String key, Long value) {
		section.set(key, value);
	}

	@Override
	public Class<Long> getClazz() {
		return Long.class;
	}

	@Override
	public Long get(ConfigurationSection section, String s) {
		return section.getLong(s);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return String.valueOf(get(section, s));
	}

	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.STICK.parseMaterial());
		return item;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		edit(editor.getFile(), editor.getValueHandler(), editor.getId(),
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrent(), key,
						editor.getHandler(key).getClazz(), 32),
				editor.settings, () -> editor.getCurrent().getLong(key), l -> editor.getCurrent().set(key, l),
				editor::open);
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte;
	}

	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			EditorSettings settings, Supplier<Long> valSup, Consumer<Long> onEdit, Runnable onBack) {
		yfe.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new LongEditor(id, title, settings, valSup, onEdit, onBack), 1);
	}
}
