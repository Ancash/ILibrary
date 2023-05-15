package de.ancash.minecraft.inventory.editor.yml.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.editor.yml.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.ListEditor;
import de.ancash.minecraft.inventory.editor.yml.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

@SuppressWarnings("rawtypes")
public class ListHandler implements IValueHandler<List> {

	public static final ListHandler INSTANCE = new ListHandler();

	protected ListHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isList(key);
	}

	@Override
	public Class<?> getClazz() {
		return List.class;
	}

	@Override
	public List get(ConfigurationSection section, String s) {
		return section.getList(s);
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getAddItem() {
		return new ItemBuilder(XMaterial.IRON_BARS).setDisplayname("§7Add List").build();
	}

	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.IRON_BARS.parseMaterial());
		return item;
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		return valueToString(get(section, s));
	}

	public String valueToString(List l) {
		StringBuilder builder = new StringBuilder();
		for (Object o : l) {
			builder.append(ChatColor.WHITE).append(o).append('\n');
		}
		return builder.toString();
	}

	@Override
	public boolean isValid(Object o) {
		return o instanceof List;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		edit(editor.getFile(), editor, key, editor.getValueHandler(), editor.getId(),
				YamlEditor.createTitle(editor.getRoot(), editor.getCurrent(), key, editor.getHandler(key).getClazz(),
						32),
				() -> editor.getCurrent().getList(key), k -> editor.getCurrent().set(key, k), () -> editor.open(),
				() -> editor.getCurrent().remove(key));
	}

	@Override
	public void edit(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, Supplier<List> valSup, Consumer<List> onEdit, Runnable onBack, Runnable onDelete) {
		ListEditor le = new ListEditor(yfe, parent, key, valHandler, id, title, yfe.getSettings(), valSup, onEdit,
				onBack, onDelete);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> le.open(), 1);
	}

	@Override
	public List defaultValue() {
		return new ArrayList<>();
	}
}
