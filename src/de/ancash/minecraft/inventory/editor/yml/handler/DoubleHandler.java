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

public class DoubleHandler implements IValueHandler<Double> {

	public static final DoubleHandler INSTANCE = new DoubleHandler();

	protected DoubleHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isDouble(key);
	}

	@Override
	public Class<?> getClazz() {
		return Double.class;
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.LADDER).setDisplayname("§7Add Double").build();
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
		edit(editor.getYamlEditor(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(), 32),
				() -> editor.getCurrent().getDouble(key), d -> editor.getCurrent().set(key, d), editor::open, () -> editor.getCurrent().remove(key));
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Double;
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id, String title,
			Supplier<Double> valSup, Consumer<Double> onEdit, Runnable onBack, Runnable onDelete) {
		DoubleEditor de = new DoubleEditor(id, title, parent, yfe, key, valSup, onEdit, onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> de.open(), 1);
	}

	@Override
	public Double defaultValue() {
		return 0d;
	}
}
