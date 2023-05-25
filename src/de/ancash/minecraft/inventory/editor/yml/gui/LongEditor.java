package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.minecraft.inventory.input.NumberInputGUI;

public class LongEditor extends ValueEditor<Long> {

	protected final Consumer<Long> onEdit;
	protected final Runnable onDelete;

	public LongEditor(UUID id, String title, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Supplier<Long> valSup, Consumer<Long> onEdit, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, parent, yeditor, key, valSup, onBack);
		this.onEdit = onEdit;
		this.onDelete = onDelete;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 12, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
		addEditorItemWithSuggestions(14, XMaterial.CHEST);
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(valSup.get())).build();
	}

	@Override
	protected void useSuggestion(ValueSuggestion<Long> sugg) {
		onEdit.accept(sugg.getSuggestion());
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
				() -> new LongEditor(getId(), title, parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
	}

	public void acceptInput() {
		NumberInputGUI<Long> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()), Long.class,
				s -> {
					onEdit.accept(s);
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new LongEditor(getId(), title,
							parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
				}, l -> {
					Optional<String> o = yeditor.isValid(this, l);
					return Tuple.of(!o.isPresent(), o.orElse(null));
				});
		nig.setLeft(XMaterial.DIRT.parseItem());
		nig.setTitle(title);
		nig.setText(valSup.get().toString());
		closeAll();
		IGUIManager.remove(id);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> nig.start(), 1);
	}

	@Override
	protected void saveListElement(Object val) {
		throw new UnsupportedOperationException();
	}
}
