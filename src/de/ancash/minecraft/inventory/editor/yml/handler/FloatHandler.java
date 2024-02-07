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
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.DoubleEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public class FloatHandler implements IValueHandler<Float> {

	public static final FloatHandler INSTANCE = new FloatHandler();

	protected FloatHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.get(key) instanceof Float;
	}

	@Override
	public Class<?> getClazz() {
		return Float.class;
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.LADDER).setDisplayname("§7Add Float").build();
	}

	@Override
	public Float get(ConfigurationSection section, String s) {
		return ((Number) section.get(s)).floatValue();
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
		edit(editor.getYamlEditor(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(), 32),
				() -> get(editor.getCurrent(), key), d -> editor.getCurrent().set(key, d), editor::open, () -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Float;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<Float> valSup, Consumer<Float> onEdit, Runnable onBack, Runnable onDelete) {
		DoubleEditor de = new DoubleEditor(id, title, parent, yfe, key, () -> valSup.get().doubleValue(), d -> onEdit.accept(d.floatValue()), onBack,
				onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> de.open(), 1);
	}

	@Override
	public Float defaultValue() {
		return 0f;
	}
}
