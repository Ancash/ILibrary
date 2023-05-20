package de.ancash.minecraft.inventory.editor.yml.suggestion;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;

public class KeySuggestion {

	protected final String key;
	protected final IValueHandler<?> handler;
	protected final Object def;
	protected final String name;

	public KeySuggestion(String key, IValueHandler<?> handler) {
		this(key, handler, null);
	}

	public KeySuggestion(String key, IValueHandler<?> handler, String name) {
		this(key, handler, null, name);
	}

	public KeySuggestion(String key, IValueHandler<?> handler, Object def) {
		this(key, handler, def, null);
	}

	@SuppressWarnings("nls")
	public KeySuggestion(String key, IValueHandler<?> handler, Object def, String name) {
		Validate.isTrue(key != null && !key.isEmpty(), "invalid key: " + key);
		Validate.notNull(handler, "no handler");
		if (def != null)
			Validate.isTrue(handler.isValid(def),
					"default value not valid (" + handler.getClass().getSimpleName() + "): " + def);
		this.key = key;
		if (name == null || name.isEmpty())
			name = handler.getClazz().getSimpleName();
		this.name = name;
		this.handler = handler;
		this.def = def;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public boolean hasDefaultValue() {
		return def != null;
	}

	public Object getDefaultValue() {
		return def;
	}

	public IValueHandler<?> getType() {
		return handler;
	}

	public void createKey(ConfigurationSectionEditor where) {
		if (def != null)
			handler.setUnchecked(where.getCurrent(), key, def);
		else
			handler.setDefaultValue(where.getCurrent(), key);
	}
}
