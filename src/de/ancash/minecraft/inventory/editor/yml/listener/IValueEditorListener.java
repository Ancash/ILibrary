package de.ancash.minecraft.inventory.editor.yml.listener;

import de.ancash.minecraft.inventory.editor.yml.ValueEditor;

public interface IValueEditorListener {

	public void onInvalidInput(ValueEditor<?> editor, Object o, String reason);

	public void onValidInput(ValueEditor<?> editor, Object o);
}
