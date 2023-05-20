package de.ancash.minecraft.inventory.editor.yml.suggestion;

import java.util.Set;

import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;

public interface IValueSuggester {

	public <T> Set<ValueSuggestion<T>> getValueSuggestions(ValueEditor<T> where);

}
