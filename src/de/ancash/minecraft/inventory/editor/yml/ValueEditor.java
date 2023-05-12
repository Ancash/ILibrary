package de.ancash.minecraft.inventory.editor.yml;

import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;

public abstract class ValueEditor<T> extends IGUI {

	protected final EditorSettings settings;
	protected final Supplier<T> valSup;
	protected Runnable onBack;

	public ValueEditor(UUID id, String title, int size, EditorSettings settings, Supplier<T> valSup, Runnable onBack) {
		super(id, size, title);
		this.settings = settings;
		this.onBack = onBack;
		this.valSup = valSup;
		for (int i = 0; i < getSize(); i++)
			setItem(settings.getBackgroundItem(), i);
		if (onBack != null)
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), getSize() - 5,
					(a, b, c, top) -> Lambda.execIf(top, this::back)));
		open();
	}

	protected ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.REDSTONE_TORCH).setDisplayname(String.valueOf(valSup.get())).build();
	}

	protected void back() {
		closeAll();
		onBack.run();
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		IGUIManager.remove(id);
		if (onBack == null)
			return;
		onBack.run();
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}
}
