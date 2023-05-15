package de.ancash.minecraft.inventory.editor.yml.listener;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.minecraft.inventory.editor.yml.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public interface IKeyValidator {

	/**
	 * If {@link Duplet#getFirst()} is null, the key is invalid and
	 * {@link Duplet#getSecond()} is the reason, if not, this is key that will be
	 * used.
	 * 
	 * @param key
	 * @return
	 */
	public Duplet<String, String> validate(ValueEditor<?> where, IValueHandler<?> type, String key);

}
