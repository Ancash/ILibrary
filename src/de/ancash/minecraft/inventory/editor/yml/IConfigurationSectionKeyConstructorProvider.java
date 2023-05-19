package de.ancash.minecraft.inventory.editor.yml;

import java.util.Set;

import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;

public interface IConfigurationSectionKeyConstructorProvider {

	public Set<ConfigurationSectionKeyConstructor> getKeyConstructor(ConfigurationSectionEditor where);

}
