package de.ancash.minecraft.inventory.editor.handler;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.input.StringInputGUI;
import net.md_5.bungee.api.ChatColor;

public class ListEditor extends ValueEditor {

	private int pos = 0;

	public ListEditor(ConfigurationSectionEditor editor, String key) {
		super(36, editor, key);
		addMainItem();
	}

	protected void addMainItem() {
		addInventoryItem(new InventoryItem(this, getEditorItem(), 10, (slot, shift, action, top) -> {
			switch (action) {
			case CLONE_STACK:
				deleteSelected();
				break;
			case PICKUP_ALL:
				nextElement();
				break;
			default:
				break;
			}
		}));
	}

	protected void prevElement() {
		pos--;
		if (pos < 0)
			pos = getList().size() - 1;
		addMainItem();
	}

	protected void nextElement() {
		pos++;
		if (pos == getList().size())
			pos = 0;
		addMainItem();
	}

	@SuppressWarnings("rawtypes")
	protected List getList() {
		return editor.getCurrentConfigurationSection().getList(key);
	}

	@SuppressWarnings("rawtypes")
	protected void deleteSelected() {
		List l = getList();
		l.remove(pos);
		editor.getCurrentConfigurationSection().set(key, l);
		pos = Math.max(0, pos - 1);
		addMainItem();
	}

	@Override
	public ItemStack getEditorItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("§eLeft click to go down").append("\n")
				.append("§eClick mouse wheel to delete the selected element").append("\n");
		for (int i = 0; i < editor.getCurrentConfigurationSection().getList(key).size(); i++) {
			builder.append(ChatColor.WHITE.toString());
			if (i == pos)
				builder.append(">");
			builder.append(editor.getCurrentConfigurationSection().getList(key).get(i));
			builder.append('\n');
		}
		return new ItemBuilder(XMaterial.CHEST).setDisplayname(key).setLore(builder.toString().split("\n")).build();
	}

	public void acceptInput() {
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()), s -> {
			section.set(key, s);
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new ListEditor(editor, key), 1);
		});
		closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> sig.open(), 1);
	}
}
