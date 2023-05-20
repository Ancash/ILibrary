package de.ancash.minecraft.inventory.editor.yml.suggestion;

import java.util.Set;

import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;

public interface IKeySuggester {

	public Set<KeySuggestion> getKeySuggestions(ConfigurationSectionEditor where);

}
