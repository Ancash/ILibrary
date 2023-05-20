package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.UUID;
import java.util.function.Supplier;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;

public class BooleanEditor extends ValueEditor<Boolean> {

	protected final Runnable onToggle;

	public BooleanEditor(UUID id, String title, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Runnable onToggle, Supplier<Boolean> valSup, Runnable onBack, Runnable onDelete) {
		super(id, title, 36, parent, yeditor, key, valSup, onBack);
		this.onToggle = onToggle;
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 12, (a, b, c, top) -> Lambda.execIf(top, this::toggle)));
		addEditorItemWithSuggestions(14, XMaterial.CHEST);
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 35, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
	}

	@Override
	protected void useSuggestion(ValueSuggestion<Boolean> sugg) {
		if (valSup.get() != sugg.getSuggestion())
			toggle();
	}

	public void toggle() {
		onToggle.run();
		addInventoryItem(new InventoryItem(this,
				ItemStackUtils.setDisplayname(settings.getBooleanItem(), String.valueOf(valSup.get())), 12,
				(a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}
}
