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
		edit(editor.getFile(), editor.getValueHandler(), editor.getId(),
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrent(), key,
						editor.getHandler(key).getClazz(), 32),
				editor.getSettings(), () -> editor.getCurrent().getDouble(key), d -> editor.getCurrent().set(key, d),
				editor::open);
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof Double || o instanceof Float;
	}

	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			EditorSettings settings, Supplier<Double> valSup, Consumer<Double> onEdit, Runnable onBack) {
		yfe.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
				() -> new DoubleEditor(id, title, settings, valSup, onEdit, onBack), 1);
	}
}
