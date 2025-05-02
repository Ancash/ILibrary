package de.ancash.nbtnexus.editor.handler;

import java.util.Set;

import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.suggestion.IValueSuggester;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureValueSuggestion;

public class ValueSuggester implements IValueSuggester {

	private final SerDeStructure structure = NBTNexus.getInstance().getStructure();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Set<ValueSuggestion<T>> getValueSuggestions(ValueEditor<T> cur) {
		ConfigurationSectionEditor root = ValidatorUtil.getItemRoot(cur);
		if (root == null || !ValidatorUtil.isItemRoot(root))
			return null;
		String path = ValidatorUtil.getPath(root, cur);

		if (structure.isMap(path))
			return null;

		SerDeStructureValueSuggestion<T> sug = null;

		if (structure.isList(path)) {
			if (structure.getList(path).getEntry() == null || structure.getList(path).getEntry().getValue() == null)
				return null;
			sug = (SerDeStructureValueSuggestion<T>) structure.getList(path).getEntry().getValue();
		} else {
			if (!structure.isEntry(path))
				return null;
			sug = (SerDeStructureValueSuggestion<T>) structure.getEntry(path).getValue();
		}
		return sug != null ? sug.getSuggestions() : null;
	}

}
