package de.ancash.minecraft.inventory.editor.yml;

import java.util.UUID;
import java.util.function.Supplier;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.InventoryItem;

public class BooleanEditor extends ValueEditor<Boolean> {

	protected final Runnable onToggle;

	public BooleanEditor(UUID id, String title, EditorSettings settings, Runnable onToggle, Supplier<Boolean> valSup,
			Runnable onBack, Runnable onDelete) {
		super(id, title, 36, settings, valSup, onBack);
		this.onToggle = onToggle;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::toggle)));
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
	}

	public void toggle() {
		onToggle.run();
		addInventoryItem(new InventoryItem(this,
				ItemStackUtils.setDisplayname(settings.getBooleanItem(), String.valueOf(valSup.get())), 13,
				(a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}
}
