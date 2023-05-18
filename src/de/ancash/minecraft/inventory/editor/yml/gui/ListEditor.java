package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.EditorSettings;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("rawtypes")
public class ListEditor extends ValueEditor<List> {

	protected int elementPos = 0;
	protected List<IValueHandler<?>> handler;
	protected final Consumer<List> onEdit;
	protected final YamlEditor yfe;
	protected int addPos = 0;
	protected final Runnable onDelete;
	protected boolean finishedConstructor = false;

	public ListEditor(YamlEditor yfe, ValueEditor<?> parent, String key, List<IValueHandler<?>> valHandler, UUID id,
			String title, EditorSettings settings, Supplier<List> valSup, Consumer<List> onEdit, Runnable onBack,
			Runnable onDelete) {
		super(id, title, 36, parent, yfe, key, valSup, onBack);
		finishedConstructor = true;
		this.onEdit = onEdit;
		this.onDelete = onDelete;
		this.handler = valHandler;
		this.yfe = yfe;
		if (yfe.getListTypeValidator() != null)
			yfe.getListTypeValidator().onInit(this);
	}

	@Override
	public void open() {
		if (!finishedConstructor)
			return;
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
		addMainItem();
		addInsertItem();
		super.open();
	}

	public void setHandler(List<IValueHandler<?>> handler) {
		this.handler = Collections.unmodifiableList(new ArrayList<>(handler));
	}

	public List<IValueHandler<?>> getHandler() {
		return handler;
	}

	@SuppressWarnings({ "unchecked", "nls" })
	protected void editSelected() {
		List l = getList0();
		if (l.size() <= elementPos)
			return;
		IValueHandler<?> ivh = yfe.getHandler(this, l.get(elementPos));
		ivh.uncheckedEdit(yfe, this, key, yfe.getValHandler(), getId(),
				YamlEditor.cut(String.join(":", title, String.valueOf(elementPos)), 32), () -> l.get(elementPos), e -> {
					l.set(elementPos, e);
					onEdit.accept(l);
				}, () -> ListHandler.INSTANCE.uncheckedEdit(yfe, this, key, handler, id, title, valSup, onEdit, onBack,
						onDelete),
				null);
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
		if (getList0().isEmpty())
			return;
		elementPos--;
		if (elementPos < 0)
			elementPos = getList0().size() - 1;
		addMainItem();
		addInsertItem();
	}

	protected void nextElement() {
		if (getList0().isEmpty())
			return;
		elementPos++;
		if (elementPos == getList0().size())
			elementPos = 0;
		addMainItem();
		addInsertItem();
	}

	protected void addInsertItem() {
		addInventoryItem(new InventoryItem(this, insertItem(), 15, (slot, shift, action, top) -> {
			if (!top)
				return;
			List list = getList0();
			switch (action) {
			case PICKUP_HALF:
				handler.get(addPos).addDefaultToList(this, list, Math.max(elementPos, 0));
				if (yfe.getListTypeValidator() != null)
					yfe.getListTypeValidator().onInsert(this, handler.get(addPos));
				break;
			case PICKUP_ALL:
				if (list.isEmpty())
					handler.get(addPos).addDefaultToList(this, list, 0);
				else
					handler.get(addPos).addDefaultToList(this, list, Math.min(elementPos + 1, list.size()));
				if (yfe.getListTypeValidator() != null)
					yfe.getListTypeValidator().onInsert(this, handler.get(addPos));
				break;
			case CLONE_STACK:
				nextAddOption();
				break;
			default:
				break;
			}
			addPos = addPos % handler.size();
			addMainItem();
			addInsertItem();
		}));
	}

	@SuppressWarnings("nls")
	protected ItemStack insertItem() {
		List<String> lore = new ArrayList<>();
		List list = getList0();
		lore.add("§eRight click to insert before");
		lore.add("§eLeft click to insert after");
		lore.add("§eMouse wheel to select type");
		lore.addAll(getSelecteTypeLore());
		lore.add("§7Index: " + elementPos);
		if (!list.isEmpty()) {
			if (elementPos > 0)
				lore.add("§fPrevious: '" + list.get(elementPos - 1) + "'");
			lore.add("§fCurrent: '" + list.get(elementPos) + "'");
			if (elementPos < list.size() - 1)
				lore.add("§fNext: '" + list.get(elementPos + 1) + "'");
		}
		return new ItemBuilder(XMaterial.ARROW).setLore(lore).build();
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		return Collections.unmodifiableList(getList0());
	}

	protected List getList0() {
		return valSup.get();
	}

	@SuppressWarnings("nls")
	protected List<String> getSelecteTypeLore() {
		List<String> lore = new ArrayList<>();
		for (int i = 0; i < handler.size(); i++) {
			IValueHandler<?> ivh = handler.get(i);
			ItemStack add = ivh.getAddItem();
			if (add == null)
				continue;
			if (i == addPos) {
				lore.add("§a" + "Add " + ivh.getClazz().getSimpleName());
			} else
				lore.add("§f" + "Add " + ivh.getClazz().getSimpleName());
		}
		return lore;
	}

	protected void nextAddOption() {
		addPos = (addPos + 1) % handler.size();
		if (handler.get(addPos).getAddItem() == null)
			nextAddOption();
	}

	protected void deleteSelected() {
		List l = getList0();
		if (l.size() <= elementPos)
			return;
		Object o = l.remove(elementPos);
		elementPos = Math.max(0, elementPos - 1);
		onEdit.accept(getList0());
		if (yfe.getListTypeValidator() != null)
			yfe.getListTypeValidator().onDelete(this, yfe.getHandler(this, o), o);
		addMainItem();
		addInsertItem();
	}

	@SuppressWarnings("nls")
	@Override
	protected ItemStack getEditorItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("§eLeft click to go down").append("\n").append("§eRight click to go up").append("\n")
				.append("§eMouse wheel to delete the selected element").append("\n")
				.append("§eShift click to edit the selected element").append("\n")
				.append("§7Syntax: [{index}][{type}]={value}").append("\n");
		for (int i = elementPos; i - elementPos < getList0().size(); i++) {
			builder.append(ChatColor.WHITE.toString());
			if (i == elementPos)
				builder.append(">");
			builder.append("[").append(i % getList0().size()).append("][")
					.append(yfe.getHandler(this, getList0().get(i % getList0().size())).getClazz().getSimpleName())
					.append("]=");
			String s = getList0().get(i % getList0().size()).toString().replace("\n", "\\n");
			builder.append("'").append(s).append("'").append('\n');
		}
		return new ItemBuilder(XMaterial.CHEST).setDisplayname(title).setLore(builder.toString().split("\n")).build();
	}
}
