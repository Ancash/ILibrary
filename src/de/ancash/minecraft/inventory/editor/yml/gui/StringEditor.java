package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.chat.input.StringChatInput;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class StringEditor extends ValueEditor<String> {

	@SuppressWarnings("nls")
	public static List<String> split(String in) {
		StringBuilder sb = new StringBuilder();
		char[] chars = in.toCharArray();
		int cnt = 0;
		for (char c : chars) {
			if (cnt++ % 50 == 0 && sb.length() > 0)
				sb.append("\n");
			sb.append(c);
		}
		return Arrays.asList(sb.toString().split("\n")).stream().map(s -> ItemStackUtils.translateChatColor(s, '&'))
				.collect(Collectors.toList());
	}

	protected final Consumer<String> onEdit;
	protected final Runnable onDelete;
	protected final List<String> value = new ArrayList<>();
	protected int pos = 0;

	public StringEditor(UUID id, String title, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Supplier<String> valSup, Consumer<String> onEdit, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, parent, yeditor, key, valSup, onBack);
		this.onEdit = onEdit;
		this.onDelete = onDelete;
		value.addAll(split(valSup.get()));
		addEditorItem(11);
		addEditorItemWithSuggestions(13, XMaterial.CHEST);
		addAddItem(15);
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
	}

	@SuppressWarnings("nls")
	public void addAddItem(int slot) {
		addInventoryItem(new InventoryItem(this, yeditor.getSettings().stringEditorSupplierItem(), slot,
				(a, shift, action, top) -> {
					if (!top)
						return;
					if (shift) {
						acceptChatInput(s -> value.addAll(pos + 1, split(s)));
						return;
					}
					switch (action) {
					case DROP_ONE_SLOT:
						acceptChatInput(s -> value.addAll(pos, split(s)));
						break;
					case PICKUP_ALL:
						value.add(Math.min(value.size(), pos + 1), "");
						break;
					case PICKUP_HALF:
						value.add(Math.max(0, pos), "");
						break;
					default:
						break;
					}
					addEditorItem(11);
				}));
	}

	public void addEditorItem(int slot) {
		addInventoryItem(new InventoryItem(this, getEditorItem(), slot, (a, shift, action, top) -> {
			if (!top)
				return;
			if (shift) {
				if (!value.isEmpty()) {
					value.remove(pos);
					if (!value.isEmpty())
						pos = pos % value.size();
					if (pos >= value.size())
						pos = 0;
					addEditorItem(slot);
				}
				return;
			}
			switch (action) {
			case PICKUP_ALL:
				if (!value.isEmpty())
					acceptAnvilInput();
				break;
			case PICKUP_HALF:
				if (!value.isEmpty())
					pos = (pos + 1) % value.size();
				addEditorItem(slot);
				break;
			default:
				break;
			}
		}));
	}

	@SuppressWarnings("nls")
	public ItemStack getEditorItem() {
		List<String> elements = new ArrayList<>();
		elements.add("§eRight click to select section");
		elements.add("§eLeft click to edit");
		elements.add("§eShift click to delete");
		for (String s : value)
			elements.add("\"" + s + "\"");
		for (int i = 3; i < elements.size(); i++) {
			if (i - 3 == pos)
				elements.set(i, "§a" + elements.get(i));
			else
				elements.set(i, "§7" + elements.get(i));
		}
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname("§7Sections").setLore(elements).build();
	}

	@Override
	protected void useSuggestion(ValueSuggestion<String> sugg) {
		onEdit.accept(sugg.getSuggestion());
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
				() -> new StringEditor(getId(), title, parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
	}

	protected void acceptChatInput(Consumer<String> onComplete) {
		StringChatInput sci = new StringChatInput(ILibrary.getInstance(), Bukkit.getPlayer(getId()));
		sci.onComplete(s -> {
			onComplete.accept(s);
			onEdit.accept(String.join("", value));
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
					() -> new StringEditor(getId(), title, parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
		});
		sci.setInitialInputMessage("§aEnter text");
		sci.isValid(isValid(yeditor, this));
		sci.start();
		closeAll();
	}

	protected void acceptAnvilInput() {
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()));
		sig.setLeft(XMaterial.DIRT.parseItem());
		sig.setTitle(title);
		sig.setText(value.get(pos));
		closeAll();
		sig.onComplete(s -> {
			value.set(pos, s);
			onEdit.accept(String.join("", value));
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
					() -> new StringEditor(getId(), title, parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
		});
		sig.isValid(isValid(yeditor, this));
		IGUIManager.remove(id);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> sig.start(), 1);
	}

	@Override
	protected void saveListElement(Object val) {
		throw new UnsupportedOperationException();
	}
}
