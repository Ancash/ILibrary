package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.editor.yml.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.LongEditor;
import de.ancash.minecraft.inventory.editor.yml.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

public class LongHandler implements IValueHandler<Long> {

	public static final LongHandler INSTANCE = new LongHandler();

	protected LongHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isLong(key) || section.isInt(key);
	}

	@Override
	public Class<?> getClazz() {
		return Long.class;
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.STICK).setDisplayname("§7Add Long").build();
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
		edit(editor.getFile(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(),
						32),
				() -> editor.getCurrent().getLong(key), l -> editor.getCurrent().set(key, l), editor::open,
				() -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<Long> valSup, Consumer<Long> onEdit, Runnable onBack, Runnable onDelete) {
		LongEditor le = new LongEditor(id, title, parent, yfe, key, valSup, onEdit, onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> le.open(), 1);
	}

	@Override
	public Long defaultValue() {
		return 0l;
	}
}
