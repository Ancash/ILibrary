package de.ancash.minecraft.inventory.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.minecraft.inventory.editor.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.handler.MapHandler;
import de.ancash.minecraft.inventory.editor.handler.StringHandler;

public class YamlFileEditor {

	public static final Set<IValueHandler<?>> DEFAULT_VALUE_HANDLER = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList(ConfigurationSectionHandler.INSTANCE, MapHandler.INSTANCE, BooleanHandler.INSTANCE,
					LongHandler.INSTANCE, DoubleHandler.INSTANCE, ListHandler.INSTANCE, StringHandler.INSTANCE)));

	protected final File file;
	protected final Player p;
	protected final YamlFile yamlFile = new YamlFile();
	protected final EditorSettings settings;
	protected final Set<IValueHandler<?>> handler;

	public YamlFileEditor(File file, Player p)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p);
	}

	public YamlFileEditor(File file, Player p, Set<IValueHandler<?>> handler)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, handler);
	}

	public YamlFileEditor(EditorSettings settings, File file, Player p)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, DEFAULT_VALUE_HANDLER);
	}

	public YamlFileEditor(EditorSettings settings, File file, Player p, Set<IValueHandler<?>> handler)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.file = file;
		this.p = p;
		this.settings = settings;
		this.handler = handler;
		load();
	}

	@SuppressWarnings("nls")
	protected void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
		StringBuilder builder = new StringBuilder();
		Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(s -> builder.append(s).append('\n'));
		yamlFile.loadFromString(builder.toString().replace("==: ", "clazz: "));
	}

	public void open() {
		ConfigurationSectionEditor editor = new ConfigurationSectionEditor(this, p, yamlFile, yamlFile, handler);
		editor.open();
	}

	public void closeAll() {
		p.closeInventory();
	}

	public EditorSettings getSettings() {
		return settings;
	}

	public Collection<IValueHandler<?>> getValHandler() {
		return handler;
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to) {
		return createTitle(root, to, 32);
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to, int max) {
		return cut(root.getCurrentPath(), max);
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key) {
		return createTitle(root, to, key, 32);
	}

	@SuppressWarnings("nls")
	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key, int max) {
		return cut(String.join(":", YamlFileEditor.createTitle(root, to), key), max);
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key, Class<?> clazz,
			int max) {
		return YamlFileEditor.cut(String.join(":", YamlFileEditor.createTitle(root, to, key), clazz.getSimpleName()),
				32);
	}

	public static String cut(String s, int max) {
		return s.substring(Math.max(0, s.length() - max));
	}
}
