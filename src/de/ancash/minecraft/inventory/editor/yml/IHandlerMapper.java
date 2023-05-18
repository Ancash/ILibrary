package de.ancash.minecraft.inventory.editor.yml;

import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public interface IHandlerMapper {

	public default IValueHandler<?> getHandler(ConfigurationSectionEditor cur, String key) {
		for (IValueHandler<?> ivh : cur.getYamlEditor().getValHandler())
			if (ivh.isValid(cur.getCurrent(), key))
				return ivh;
		return null;
	}

	public default IValueHandler<?> getHandler(ValueEditor<?> cur, Object o) {
		for (IValueHandler<?> ivh : cur.getYamlEditor().getValHandler())
			if (ivh.isValid(o))
				return ivh;
		return null;
	}
}
