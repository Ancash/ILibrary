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
import java.util.function.Consumer;

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
	protected final String root;
	protected final EditorSettings settings;
	protected final Set<IValueHandler<?>> handler;
	protected final Consumer<YamlFileEditor> onSave;

	public YamlFileEditor(File file, Player p, Consumer<YamlFileEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, onSave);
	}

	public YamlFileEditor(File file, Player p, String root, Consumer<YamlFileEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, root, onSave);
	}

	public YamlFileEditor(File file, Player p, Set<IValueHandler<?>> handler, Consumer<YamlFileEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, handler, onSave);
	}

	public YamlFileEditor(File file, Player p, Set<IValueHandler<?>> handler, String root,
			Consumer<YamlFileEditor> onSave) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, handler, root, onSave);
	}

	public YamlFileEditor(EditorSettings settings, File file, Player p, Consumer<YamlFileEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, DEFAULT_VALUE_HANDLER, onSave);
	}

	public YamlFileEditor(EditorSettings settings, File file, Player p, String root, Consumer<YamlFileEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, DEFAULT_VALUE_HANDLER, root, onSave);
	}

	@SuppressWarnings("nls")
	public YamlFileEditor(EditorSettings settings, File file, Player p, Set<IValueHandler<?>> handler,
			Consumer<YamlFileEditor> onSave) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, handler, "", onSave);
	}

	public YamlFileEditor(EditorSettings settings, File file, Player p, Set<IValueHandler<?>> handler, String root,
			Consumer<YamlFileEditor> onSave) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.file = file;
		this.root = root;
		this.p = p;
		this.settings = settings;
		this.handler = handler;
		this.onSave = onSave;
		load();
	}

	@SuppressWarnings("nls")
	protected void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
		StringBuilder builder = new StringBuilder();
		Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(s -> builder.append(s).append('\n'));
		yamlFile.loadFromString(builder.toString().replace("==: ", "clazz: "));
		if (!yamlFile.isConfigurationSection(root))
			throw new IllegalStateException(root + " is not configuration section");
		yamlFile.setConfigurationFile(file);
	}

	public void open() {
		ConfigurationSectionEditor editor = new ConfigurationSectionEditor(this, p,
				yamlFile.getConfigurationSection(root), yamlFile.getConfigurationSection(root), handler, null);
		editor.open();
	}

	protected Consumer<YamlFileEditor> getOnSave() {
		return onSave;
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}

	public EditorSettings getSettings() {
		return settings;
	}

	public Collection<IValueHandler<?>> getValHandler() {
		return handler;
	}

	public ConfigurationSection getRoot() {
		return yamlFile.getConfigurationSection(root);
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to) {
		return createTitle(root, to, 32);
	}

	@SuppressWarnings("nls")
	public static String createTitle(ConfigurationSection root, ConfigurationSection to, int max) {
		if (root.getCurrentPath().isEmpty()) {
			if (to.getCurrentPath().isEmpty())
				return "~";
			return cut("~." + to.getCurrentPath(), max);
		}
		return cut(to.getCurrentPath().replace(root.getCurrentPath(), "~"), max);
	}

	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key) {
		return createTitle(root, to, key, 32);
	}

	@SuppressWarnings("nls")
	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key, int max) {
		return cut(String.join(":", YamlFileEditor.createTitle(root, to), key), max);
	}

	@SuppressWarnings("nls")
	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key, Class<?> clazz,
			int max) {
		return YamlFileEditor.cut(String.join(":", YamlFileEditor.createTitle(root, to, key), clazz.getSimpleName()),
				32);
	}

	public static String cut(String s, int max) {
		return s.substring(Math.max(0, s.length() - max));
	}
}
