package de.ancash.minecraft.inventory.editor.yml;

import java.util.List;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;

public class KVConstructor {

	protected final String key;
	protected final IValueHandler<?> handler;
	protected final Object def;

	public KVConstructor(String key, IValueHandler<?> handler) {
		this(key, handler, null);
	}

	@SuppressWarnings("nls")
	public KVConstructor(String key, IValueHandler<?> handler, Object def) {
		Validate.isTrue(key != null && !key.isEmpty(), "invalid key: " + key);
		Validate.notNull(handler, "no handler");
		if (def != null)
			Validate.isTrue(handler.isValid(def),
					"default value not valid (" + handler.getClass().getSimpleName() + "): " + def);
		this.key = key;
		this.handler = handler;
		this.def = def;
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

	public IValueHandler<?> getHandler() {
		return handler;
	}

	public void createKey(ConfigurationSectionEditor where) {
		if (def != null)
			handler.setUnchecked(where.getCurrent(), key, def);
		else
			handler.setDefaultValue(where.getCurrent(), key);
	}

	public static KVConstructor forBoolean(String key) {
		return forBoolean(key, null);
	}

	public static KVConstructor forBoolean(String key, Boolean def) {
		return createNew(key, BooleanHandler.INSTANCE, def);
	}

	public static KVConstructor forDouble(String key) {
		return forDouble(key, null);
	}

	public static KVConstructor forDouble(String key, Double def) {
		return createNew(key, DoubleHandler.INSTANCE, def);
	}

	public static KVConstructor forLong(String key) {
		return forLong(key, null);
	}

	public static KVConstructor forLong(String key, Long def) {
		return createNew(key, LongHandler.INSTANCE, null);
	}

	public static KVConstructor forString(String key) {
		return forString(key, null);
	}

	public static KVConstructor forString(String key, String def) {
		return createNew(key, StringHandler.INSTANCE, def);
	}

	public static KVConstructor forConfigurationSection(String key) {
		return forConfigurationSection(key, null);
	}

	public static KVConstructor forConfigurationSection(String key, ConfigurationSection def) {
		return createNew(key, ConfigurationSectionHandler.INSTANCE, def);
	}

	public static KVConstructor forList(String key) {
		return forList(key, null);
	}

	@SuppressWarnings("rawtypes")
	public static KVConstructor forList(String key, List def) {
		return createNew(key, ListHandler.INSTANCE, def);
	}

	protected static KVConstructor createNew(String key, IValueHandler<?> handler, Object def) {
		return new KVConstructor(key, handler, def);
	}
}
