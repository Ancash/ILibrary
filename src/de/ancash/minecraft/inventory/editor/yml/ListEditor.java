package de.ancash.minecraft.inventory.editor.yml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("rawtypes")
public class ListEditor<T> extends ValueEditor<List> {

	protected int pos = 0;
	protected final Collection<IValueHandler<?>> valHandler;
	protected final Consumer<List> onEdit;
	protected final YamlFileEditor yfe;
	protected final IValueHandler<T> type;
	protected final Runnable onDelete;

	@SuppressWarnings({ "unchecked", "nls" })
	public ListEditor(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			EditorSettings settings, Supplier<List> valSup, Consumer<List> onEdit, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, settings, valSup, onBack);
		this.onEdit = onEdit;
		type = (IValueHandler<T>) ListHandler.INSTANCE.getListType(valSup.get(), valHandler);
		this.onDelete = onDelete;
		if (type == null) {
			closeAll();
			throw new IllegalStateException("could not match " + valSup.toString().getClass());
		}
		this.valHandler = valHandler;
		this.yfe = yfe;
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
		addMainItem();
		addInsertItem();
	}

	@SuppressWarnings({ "unchecked", "nls" })
	protected void editSelected() {
		List l = getList();
		if (l.size() <= pos)
			return;
		type.edit(yfe, yfe.getValHandler(), getId(),
				YamlFileEditor.cut(String.join(":", title, String.valueOf(pos)), 32), () -> (T) l.get(pos), e -> {
					l.set(pos, e);
					onEdit.accept(l);
				}, () -> ListHandler.INSTANCE.edit(yfe, valHandler, id, title, valSup, onEdit, onBack, onDelete), null);
	}

	protected void addMainItem() {
		addInventoryItem(new InventoryItem(this, getEditorItem(), 11, (slot, shift, action, top) -> {
			if (!top)
				return;
			if (shift) {
				editSelected();
				return;
			}
			switch (action) {
			case CLONE_STACK:
				deleteSelected();
				return;
			case PICKUP_ALL:
				nextElement();
				return;
			case PICKUP_HALF:
				prevElement();
				return;
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
		addInsertItem();
	}

	protected void nextElement() {
		pos++;
		if (pos == getList().size())
			pos = 0;
		addMainItem();
		addInsertItem();
	}

	@SuppressWarnings("unchecked")
	protected void addInsertItem() {
		addInventoryItem(new InventoryItem(this, insertItem(), 15, (slot, shift, action, top) -> {
			if (!top)
				return;
			List list = getList();
			switch (action) {
			case PICKUP_HALF:
				list.add(Math.max(pos, 0), type.defaultValue());
				break;
			case PICKUP_ALL:
				list.add(Math.min(pos + 1, list.size() - 1), type.defaultValue());
				break;
			default:
				break;
			}
			addMainItem();
			addInsertItem();
		}));
	}

	@SuppressWarnings("nls")
	protected ItemStack insertItem() {
		List<String> lore = new ArrayList<>();
		List list = getList();
		lore.add("§eRight click to insert before");
		lore.add("§eLeft click to insert after");
		lore.add("§7Index: " + pos);
		if (pos > 0)
			lore.add("§fPrevious: '" + list.get(pos - 1) + "'");
		lore.add("§fCurrent: '" + list.get(Math.min(pos, getList().size() - 1)) + "'");
		if (pos < list.size() - 1)
			lore.add("§fNext: '" + list.get(pos + 1) + "'");
		return new ItemBuilder(XMaterial.ARROW).setLore(lore).build();
	}

	@SuppressWarnings("unchecked")
	protected List<T> getList() {
		return valSup.get();
	}

	protected void deleteSelected() {
		List l = getList();
		if (l.size() <= pos)
			return;
		l.remove(pos);
		pos = Math.max(0, pos - 1);
		addMainItem();
		onEdit.accept(getList());
	}

	@SuppressWarnings("nls")
	@Override
	protected ItemStack getEditorItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("§eLeft click to go down").append("\n").append("§eRight click to go up").append("\n")
				.append("§eClick mouse wheel to delete the selected element").append("\n")
				.append("§eShift click to edit the selected element").append("\n")
				.append("§7Type: " + type.getClazz().getSimpleName()).append("\n").append("§7Syntax: [{index}]={value}")
				.append("\n");
		for (int i = pos; i - pos < getList().size(); i++) {
			builder.append(ChatColor.WHITE.toString());
			if (i == pos)
				builder.append(">");
			builder.append("[").append(i % getList().size()).append("]=");
			String s = getList().get(i % getList().size()).toString().replace("\n", "\\n");
			builder.append("'").append(s).append("'").append('\n');
		}
		return new ItemBuilder(XMaterial.CHEST).setDisplayname(title).setLore(builder.toString().split("\n")).build();
	}
}
