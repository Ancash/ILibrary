package de.ancash.minecraft.inventory.editor;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.input.NumberInputGUI;

public class DoubleEditor extends ValueEditor<Double> {

	protected final Consumer<Double> onEdit;
	protected final Runnable onDelete;

	public DoubleEditor(UUID id, String title, EditorSettings settings, Supplier<Double> valSup,
			Consumer<Double> onEdit, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, settings, valSup, onBack);
		this.onDelete = onDelete;
		this.onEdit = onEdit;
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
		NumberInputGUI<Double> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()),
				Double.class, s -> {
					onEdit.accept(s);
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
							() -> new DoubleEditor(getId(), title, settings, valSup, onEdit, onBack, onDelete), 1);
				});
		nig.setLeft(XMaterial.DIRT.parseItem());
		nig.setTitle(title);
		nig.setText(valSup.get().toString());
		closeAll();
		IGUIManager.remove(id);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> nig.start(), 1);
	}
}
