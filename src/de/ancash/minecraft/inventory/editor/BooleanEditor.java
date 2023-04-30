package de.ancash.minecraft.inventory.editor;

import java.util.UUID;
import java.util.function.Supplier;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.InventoryItem;

public class BooleanEditor extends ValueEditor<Boolean> {

	protected final Runnable onToggle;

	public BooleanEditor(UUID id, String title, EditorSettings settings, Runnable onToggle, Supplier<Boolean> valSup,
			Runnable onBack) {
		super(id, title, 36, settings, valSup, onBack);
		this.onToggle = onToggle;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}

	public void toggle() {
		onToggle.run();
		addInventoryItem(new InventoryItem(this,
				ItemStackUtils.setDisplayname(settings.getBooleanItem(), String.valueOf(valSup.get())), 13,
				(a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}
}
