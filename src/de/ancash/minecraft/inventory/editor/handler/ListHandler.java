package de.ancash.minecraft.inventory.editor.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.EditorSettings;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

@SuppressWarnings("rawtypes")
public class ListHandler implements IValueHandler<List> {

	public static final ListHandler INSTANCE = new ListHandler();

	ListHandler() {
	}

	@Override
	public boolean isValid(ConfigurationSection section, String key) {
		return section.isList(key);
	}

	@Override
	public void set(ConfigurationSection section, String key, List value) {
		section.set(key, value);
	}

	@Override
	public Class<List> getClazz() {
		return List.class;
	}

	@Override
	public List get(ConfigurationSection section, String s) {
		return section.getList(s);
	}

	@SuppressWarnings("nls")
	@Override
	public ItemStack getEditItem(ConfigurationSectionEditor editor, String key) {
		ItemStack item = IValueHandler.super.getEditItem(editor, key);
		item.setType(XMaterial.IRON_BARS.parseMaterial());
		List<String> lore = item.getItemMeta().getLore();
		lore.set(1, lore.get(1) + "<"
				+ getListType(get(editor.getCurrent(), key), editor.getValueHandler()).getClazz().getSimpleName()
				+ ">");
		ItemStackUtils.setLore(lore, item);
		return item;
	}

	@SuppressWarnings("nls")
	public IValueHandler<?> getListType(List l, Collection<IValueHandler<?>> valHandler) {
		List<IValueHandler<?>> handler = new ArrayList<>(valHandler);
		for (Object o : l) {
			Iterator<IValueHandler<?>> iter = handler.iterator();
			IValueHandler<?> ivh = null;
			while (iter.hasNext()) {
				ivh = iter.next();
				if (!ivh.isValid(o))
					iter.remove();
			}
		}
		if (handler.isEmpty())
			throw new IllegalStateException("could not determin list type of: \n" + valueToString(l));

		return handler.get(0);
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
		edit(editor.getFile(), editor.getValueHandler(), editor.getId(),
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrent(), key,
						editor.getHandler(key).getClazz(), 32),
				editor.settings, () -> editor.getCurrent().getList(key), k -> Function.identity(), editor::open);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edit(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			EditorSettings settings, Supplier<List> valSup, Consumer<List> onEdit, Runnable onBack) {
		yfe.closeAll();
		new ListEditor(yfe, valHandler, id, title, settings, valSup, onEdit, onBack);
	}

}
