package de.ancash.minecraft.inventory.editor.yml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.minecraft.inventory.editor.yml.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.MapHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.listener.IValueEditorListener;

public class YamlEditor {

	private static final List<IValueHandler<?>> DEFAULT_VALUE_HANDLER = Arrays.asList(
			ConfigurationSectionHandler.INSTANCE, MapHandler.INSTANCE, BooleanHandler.INSTANCE, LongHandler.INSTANCE,
			DoubleHandler.INSTANCE, ListHandler.INSTANCE, StringHandler.INSTANCE);

	public static List<IValueHandler<?>> getDefaultHandler() {
		return Collections.unmodifiableList(DEFAULT_VALUE_HANDLER);
	}

	public static synchronized void addValueHandler(IValueHandler<?> handler) {
		DEFAULT_VALUE_HANDLER.add(0, handler);
	}

	protected final File file;
	protected final Player p;
	protected final YamlFile yamlFile = new YamlFile();
	protected final String root;
	protected final EditorSettings settings;
	protected final List<IValueHandler<?>> handler;
	protected final Consumer<YamlEditor> onSave;
	protected final Set<IValueEditorListener> listener = new HashSet<>();
	protected final Set<AbstractInputValidator<?>> validator = new HashSet<>();

	public YamlEditor(File file, Player p, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, onSave);
	}

	public YamlEditor(String yaml, Player p, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, yaml, p, onSave);
	}

	public YamlEditor(File file, Player p, String root, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, file, p, root, onSave);
	}

	public YamlEditor(String yaml, Player p, String root, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(new EditorSettings() {
		}, yaml, p, root, onSave);
	}

	@SuppressWarnings("nls")
	public YamlEditor(EditorSettings settings, File file, Player p, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, "", onSave);
	}

	@SuppressWarnings("nls")
	public YamlEditor(EditorSettings settings, String yaml, Player p, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, yaml, p, "", onSave);
	}

	public YamlEditor(EditorSettings settings, File file, Player p, String root, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, file, p, Collections.unmodifiableList(DEFAULT_VALUE_HANDLER), root, onSave);
	}

	public YamlEditor(EditorSettings settings, String yaml, Player p, String root, Consumer<YamlEditor> onSave)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		this(settings, yaml, p, Collections.unmodifiableList(DEFAULT_VALUE_HANDLER), root, onSave);
	}

	@SuppressWarnings("nls")
	public YamlEditor(EditorSettings settings, File file, Player p, List<IValueHandler<?>> handler, String root,
			Consumer<YamlEditor> onSave) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.file = file;
		this.root = root;
		this.p = p;
		this.settings = settings;
		this.handler = handler;
		this.onSave = onSave;
		StringBuilder builder = new StringBuilder();
		Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(s -> builder.append(s).append('\n'));
		yamlFile.loadFromString(builder.toString().replace("==: ", "clazz: "));
		if (!yamlFile.isConfigurationSection(root))
			throw new IllegalStateException(root + " is not configuration section");
		yamlFile.setConfigurationFile(file);
	}

	@SuppressWarnings("nls")
	public YamlEditor(EditorSettings settings, String yaml, Player p, List<IValueHandler<?>> handler, String root,
			Consumer<YamlEditor> onSave) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.root = root;
		this.p = p;
		this.settings = settings;
		this.handler = handler;
		this.onSave = onSave;
		this.file = null;
		yamlFile.loadFromString(yaml.replace("==: ", "clazz: "));
		if (!yamlFile.isConfigurationSection(root))
			throw new IllegalStateException(root + " is not configuration section");
	}

	public void open() {
		ConfigurationSectionEditor editor = new ConfigurationSectionEditor(this, null, null, p,
				yamlFile.getConfigurationSection(root), yamlFile.getConfigurationSection(root), handler, null);
		editor.open();
	}

	public void addValidator(AbstractInputValidator<?> aiv) {
		validator.add(aiv);
	}

	public Optional<String> isValid(ValueEditor<?> ve, Object o) {
		if (validator.isEmpty()) {
			getListener().forEach(ivel -> ivel.onValidInput(ve, o));
			return Optional.empty();
		}
		for (AbstractInputValidator<?> aiv : validator) {
			if (!aiv.isOfInterest(ve))
				continue;
			Optional<String> opt = aiv.isValidUnchecked(ve, o);
			if (opt.isPresent()) {
				getListener().forEach(ivel -> ivel.onInvalidInput(ve, o, opt.get()));
				return opt;
			}
		}
		getListener().forEach(ivel -> ivel.onValidInput(ve, o));
		return Optional.empty();
	}

	public void addListener(IValueEditorListener ivel) {
		listener.add(ivel);
	}

	public Set<IValueEditorListener> getListener() {
		return listener;
	}

	public IValueHandler<?> getHandler(Object o) {
		for (IValueHandler<?> ivh : handler)
			if (ivh.isValid(o))
				return ivh;
		return null;
	}

	public IValueHandler<?> getHandler(ConfigurationSection cs, String key) {
		for (IValueHandler<?> ivh : handler)
			if (ivh.isValid(cs, key))
				return ivh;
		return null;
	}

	protected Consumer<YamlEditor> getOnSave() {
		return onSave;
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}

	public EditorSettings getSettings() {
		return settings;
	}

	public List<IValueHandler<?>> getValHandler() {
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
		return cut(String.join(":", YamlEditor.createTitle(root, to), key), max);
	}

	@SuppressWarnings("nls")
	public static String createTitle(ConfigurationSection root, ConfigurationSection to, String key, Class<?> clazz,
			int max) {
		return YamlEditor.cut(String.join(":", YamlEditor.createTitle(root, to, key), clazz.getSimpleName()), 32);
	}

	public static String cut(String s, int max) {
		return s.substring(Math.max(0, s.length() - max));
	}
}
