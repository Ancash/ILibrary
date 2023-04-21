package de.ancash.minecraft.inventory.editor.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

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
		lore.set(1, lore.get(1) + "<" + getListType(editor, key).getClazz().getSimpleName() + ">");
		ItemStackUtils.setLore(lore, item);
		return item;
	}

	@Override
	public void edit(ConfigurationSectionEditor editor, String key) {
		editor.closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new ListEditor(editor, key), 1);
	}

	@SuppressWarnings("nls")
	public IValueHandler<?> getListType(ConfigurationSectionEditor editor, String key) {
		List l = get(editor.getCurrentConfigurationSection(), key);
		List<IValueHandler<?>> handler = new ArrayList<>(editor.getAllHandler());
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
			throw new IllegalStateException("could not determin list type of: \n"
					+ valueToString(editor.getCurrentConfigurationSection(), key));

		return handler.get(0);
	}

	@Override
	public String valueToString(ConfigurationSection section, String s) {
		List l = get(section, s);
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

}
