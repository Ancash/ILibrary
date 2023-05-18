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
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.LongEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public class ShortHandler implements IValueHandler<Short> {

	public static final ShortHandler INSTANCE = new ShortHandler();

	protected ShortHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.get(key) instanceof Short;
	}

	@Override
	public Class<?> getClazz() {
		return Short.class;
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.STICK).setDisplayname("§7Add Short").build();
	}

	@Override
	public Short get(ConfigurationSection section, String s) {
		return ((Number) section.get(s)).shortValue();
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
	public void uncheckedEdit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler,
			UUID id, String title, Supplier<?> valSup, Consumer<?> onEdit, Runnable onBack, Runnable onDelete) {
		IValueHandler.super.uncheckedEdit(yfe, parent, key, valHandler, id, title,
				() -> ((Number) valSup.get()).shortValue(), onEdit, onBack, onDelete);
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		edit(editor.getFile(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(),
						32),
				() -> get(editor.getCurrent(), key), l -> editor.getCurrent().set(key, l), editor::open,
				() -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Short;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<Short> valSup, Consumer<Short> onEdit, Runnable onBack, Runnable onDelete) {
		LongEditor le = new LongEditor(id, title, parent, yfe, key, () -> valSup.get().longValue(),
				l -> onEdit.accept(l.shortValue()), onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> le.open(), 1);
	}

	@Override
	public Short defaultValue() {
		return 0;
	}

}
