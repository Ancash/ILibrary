package de.ancash.nbtnexus.editor.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.IKeySuggester;
import de.ancash.minecraft.inventory.editor.yml.suggestion.KeySuggestion;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class KeySuggester implements IKeySuggester {

	private final SerDeStructure structure = NBTNexus.getInstance().getStructure();

	@Override
	public Set<KeySuggestion> getKeySuggestions(ConfigurationSectionEditor cur) {
		ConfigurationSectionEditor root = ValidatorUtil.getItemRoot(cur);
		if (root == null || !ValidatorUtil.isItemRoot(root))
			return null;
		String path = ValidatorUtil.getPath(root, cur);
		SerDeStructure base = structure.getMap(path);
		if (path.isEmpty())
			base = structure;
		else if (!structure.containsKey(path))
			return null;
		else if (structure.isList(path) || structure.isMap(path))
			base = (SerDeStructure) structure.get(path);
		Set<KeySuggestion> suggestions = new HashSet<>();
		for (String sug : base.getKeys(false)) {
			if (base.isMap(sug))
				suggestions.add(new KeySuggestion(sug, ConfigurationSectionHandler.INSTANCE, null, base.getMap(sug).toString()));
			else if (base.isList(sug)) {
				SerDeStructure list = base.getList(sug);
				suggestions.add(new KeySuggestion(sug, ListHandler.INSTANCE,
						new ArrayList<>(Arrays.asList(list.getListType().getHandler().defaultValue())), list.toString()));
			} else
				suggestions.add(new KeySuggestion(sug, base.getEntry(sug).getKey().getType().getHandler()));
		}
		return suggestions;
	}

}
