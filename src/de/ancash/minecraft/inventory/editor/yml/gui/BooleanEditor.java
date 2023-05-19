package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.UUID;
import java.util.function.Supplier;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

public class BooleanEditor extends ValueEditor<Boolean> {

	protected final Runnable onToggle;

	public BooleanEditor(UUID id, String title, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Runnable onToggle, Supplier<Boolean> valSup, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, parent, yeditor, key, valSup, onBack);
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