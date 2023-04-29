package de.ancash.minecraft.inventory.editor.handler;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.EditorSettings;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("rawtypes")
public class ListEditor<T> extends ValueEditor<List> {

	protected int pos = 0;
	protected final Collection<IValueHandler<?>> valHandler;
	protected final Consumer<List> onEdit;
	protected final YamlFileEditor yfe;
	protected final IValueHandler<T> type;

	@SuppressWarnings({ "unchecked", "nls" })
	public ListEditor(YamlFileEditor yfe, Collection<IValueHandler<?>> valHandler, UUID id, String title,
			EditorSettings settings, Supplier<List> valSup, Consumer<List> onEdit, Runnable onBack) {
		super(id,
				YamlFileEditor.cut(title + "<"
						+ ListHandler.INSTANCE.getListType(valSup.get(), valHandler).getClazz().getSimpleName() + ">",
						32),
				36, settings, valSup, onBack);
		this.onEdit = onEdit;
		type = (IValueHandler<T>) ListHandler.INSTANCE.getListType(valSup.get(), valHandler);
		if (type == null) {
			closeAll();
			throw new IllegalStateException("could not match " + valSup.toString().getClass());
		}
		this.valHandler = valHandler;
		this.yfe = yfe;
		addMainItem();
	}

	@SuppressWarnings({ "unchecked", "nls" })
	protected void editSelected() {
		List l = getList();
		if (l.size() <= pos)
			return;
		type.edit(yfe, yfe.getValHandler(), getId(),
				YamlFileEditor.cut(String.join(":", title, String.valueOf(pos)), 32), settings, () -> (T) l.get(pos),
				e -> l.set(pos, e),
				() -> ListHandler.INSTANCE.edit(yfe, valHandler, id, title, settings, valSup, onEdit, onBack));
	}

	protected void addMainItem() {
		addInventoryItem(new InventoryItem(this, getEditorItem(), 10, (slot, shift, action, top) -> {
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
	public ItemStack getEditorItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("§eLeft click to go down").append("\n")
				.append("§eClick mouse wheel to delete the selected element").append("\n")
				.append("§eShift click to edit the selected element").append("\n");
		for (int i = pos; i - pos < getList().size(); i++) {
			builder.append(ChatColor.WHITE.toString());
			if (i == pos)
				builder.append(">");
			builder.append(getList().get(i % getList().size()));
			builder.append('\n');
			if (i == getList().size() - 1) {
				builder.append("§f§l^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^").append("\n");
			}
		}
		return new ItemBuilder(XMaterial.CHEST).setDisplayname(title).setLore(builder.toString().split("\n")).build();
	}
}
