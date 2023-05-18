package de.ancash.minecraft.inventory.editor.yml;

import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public interface IValueEditorListener {

	public void onInvalidInput(ValueEditor<?> editor, Object o, String reason);

	public void onValidInput(ValueEditor<?> editor, Object o);
}
