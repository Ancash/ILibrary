package de.ancash.minecraft.inventory.editor.yml.listener;

import de.ancash.minecraft.inventory.editor.yml.ListEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public interface IListTypeValidator {

	public void onInsert(ListEditor le, IValueHandler<?> type);

	public void onInit(ListEditor le);
}
