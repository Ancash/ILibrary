package de.ancash.minecraft.inventory.editor.handler;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.EditorSettings;
import de.ancash.minecraft.inventory.input.NumberInputGUI;

public class LongEditor extends ValueEditor<Long> {

	protected final Consumer<Long> onEdit;

	public LongEditor(UUID id, String title, EditorSettings settings, Supplier<Long> valSup, Consumer<Long> onEdit,
			Runnable onBack) {
		super(id, title, 36, settings, valSup, onBack);
		this.onEdit = onEdit;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(valSup.get())).build();
	}

	public void acceptInput() {
		NumberInputGUI<Long> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()), Long.class,
				s -> {
					onEdit.accept(s);
					closeAll();
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(),
							() -> new LongEditor(getId(), title, settings, valSup, onEdit, onBack), 1);
				});
		nig.setLeft(XMaterial.DIRT.parseItem());
		nig.setTitle(title);
		nig.setText(valSup.get().toString().toString());
		closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> nig.start(), 1);
	}
}
