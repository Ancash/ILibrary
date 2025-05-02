package de.ancash.minecraft.inventory.editor.yml;

import de.ancash.minecraft.inventory.editor.yml.gui.ListEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public interface IListEditorListener {

	public void onInsert(ListEditor le, IValueHandler<?> type);

	public void onDelete(ListEditor le, IValueHandler<?> type, Object val);

	public void onInit(ListEditor le);
}
