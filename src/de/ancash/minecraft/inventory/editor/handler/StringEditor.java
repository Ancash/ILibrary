package de.ancash.minecraft.inventory.editor.handler;

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
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.EditorSettings;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class StringEditor extends ValueEditor<String> {

	protected final Consumer<String> onEdit;

	public StringEditor(UUID id, String title, EditorSettings settings, Supplier<String> valSup,
			Consumer<String> onEdit, Runnable onBack) {
		super(id, title, 36, settings, valSup, onBack);
		this.onEdit = onEdit;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(valSup.get())).build();
	}

	public void acceptInput() {
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()), s -> {
			onEdit.accept(s);
			closeAll();
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
					() -> new StringEditor(getId(), title, settings, valSup, onEdit, onBack), 1);
		});
		sig.setLeft(XMaterial.DIRT.parseItem());
		sig.setTitle(title);
		sig.setText(valSup.get().toString());
		sig.isValid(str -> Tuple.of(str != null, null));
		closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> sig.open(), 1);
	}
}
