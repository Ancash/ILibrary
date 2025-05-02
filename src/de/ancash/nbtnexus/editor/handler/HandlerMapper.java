package de.ancash.nbtnexus.editor.handler;

import de.ancash.minecraft.inventory.editor.yml.IHandlerMapper;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class HandlerMapper implements IHandlerMapper {

	private final SerDeStructure structure = NBTNexus.getInstance().getStructure();

	@Override
	public IValueHandler<?> getHandler(ConfigurationSectionEditor cur, String key) {
		ConfigurationSectionEditor root = ValidatorUtil.getItemRoot(cur);
		if (root == null || !ValidatorUtil.isItemRoot(root))
			return IHandlerMapper.super.getHandler(cur, key);

		String path = ValidatorUtil.getPath(root, cur, key);

		if (!structure.containsKey(path))
			return IHandlerMapper.super.getHandler(cur, key);

		if (structure.isMap(path))
			return ConfigurationSectionHandler.INSTANCE;
		else if (structure.isList(path))
			return ListHandler.INSTANCE;
		else
			return structure.getEntry(path).getKey().getType().getHandler();
	}
}
