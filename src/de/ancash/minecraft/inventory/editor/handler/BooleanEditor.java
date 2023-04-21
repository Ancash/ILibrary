package de.ancash.minecraft.inventory.editor.handler;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;

public class BooleanEditor extends ValueEditor {

	public BooleanEditor(ConfigurationSectionEditor editor, String key) {
		super(36, editor, key);
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}

	protected void toggle() {
		section.set(key, !section.getBoolean(key));
		addInventoryItem(new InventoryItem(this,
				ItemStackUtils.setDisplayname(editor.getSettings().getBooleanItem(),
						String.valueOf(section.getBoolean(key))),
				13, (a, b, c, top) -> Lambda.execIf(top, this::toggle)));
	}
}
