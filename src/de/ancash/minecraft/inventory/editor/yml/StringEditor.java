package de.ancash.minecraft.inventory.editor.yml;

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
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class StringEditor extends ValueEditor<String> {

	protected final Consumer<String> onEdit;
	protected final Runnable onDelete;

	public StringEditor(UUID id, String title, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Supplier<String> valSup, Consumer<String> onEdit, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, parent, yeditor, key, valSup, onBack);
		this.onEdit = onEdit;
		this.onDelete = onDelete;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
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

	public void acceptInput() {
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()), s -> {
			onEdit.accept(s);
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
					() -> new StringEditor(getId(), title, parent, yeditor, key, valSup, onEdit, onBack, onDelete), 1);
		}, in -> {
			Optional<String> o = yeditor.isValid(this, in);
			return Tuple.of(!o.isPresent(), o.orElse(null));
		});
		sig.setLeft(XMaterial.DIRT.parseItem());
		sig.setTitle(title);
		sig.setText(valSup.get().toString());
		closeAll();
		IGUIManager.remove(id);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> sig.open(), 1);
	}
}
