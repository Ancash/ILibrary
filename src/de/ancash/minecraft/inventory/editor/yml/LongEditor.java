package de.ancash.minecraft.inventory.editor.yml;

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

public class LongEditor extends ValueEditor<Long> {

	protected final Consumer<Long> onEdit;
	protected final Runnable onDelete;

	public LongEditor(UUID id, String title, EditorSettings settings, Supplier<Long> valSup, Consumer<Long> onEdit,
			Runnable onBack, Runnable onDelete) {
		super(id, title, 36, settings, valSup, onBack);
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
		NumberInputGUI<Long> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()), Long.class,
				s -> {
					onEdit.accept(s);
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
							() -> new LongEditor(getId(), title, settings, valSup, onEdit, onBack, onDelete), 1);
				});
		nig.setLeft(XMaterial.DIRT.parseItem());
		nig.setTitle(title);
		nig.setText(valSup.get().toString().toString());
		closeAll();
		IGUIManager.remove(id);
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> nig.start(), 1);
	}
}
