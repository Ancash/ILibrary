package de.ancash.minecraft.inventory.editor.yml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import de.ancash.ILibrary;
import de.ancash.minecraft.inventory.editor.yml.gui.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ListEditor;
import de.ancash.minecraft.inventory.editor.yml.gui.ValueEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ByteHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.FloatHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.IntegerHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ListHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.MapHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.ShortHandler;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.IKeySuggester;
import de.ancash.minecraft.inventory.editor.yml.suggestion.IValueSuggester;

public class YamlEditor {

	public static final IHandlerMapper DEFAULT_HANDLER_MAPPER = new IHandlerMapper() {
	};
	private static final List<IValueHandler<?>> DEFAULT_VALUE_HANDLER = Arrays.asList(
			ConfigurationSectionHandler.INSTANCE, MapHandler.INSTANCE, BooleanHandler.INSTANCE, ByteHandler.INSTANCE,
			ShortHandler.INSTANCE, IntegerHandler.INSTANCE, LongHandler.INSTANCE, FloatHandler.INSTANCE,
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
	protected final ArrayList<IValueHandler<?>> handler;
	protected final Consumer<YamlEditor> onSave;
	protected final HashSet<IValueEditorListener> listener = new HashSet<>();
	protected final HashSet<AbstractInputValidator<?>> validator = new HashSet<>();
	protected final ArrayList<IKeySuggester> keySuggester = new ArrayList<>();
	protected final ArrayList<IValueSuggester> valueSuggester = new ArrayList<>();
	protected IListEditorListener listEditorListener;
	protected IKeyValidator keyValidator;
	protected IHandlerMapper handlerMapper = DEFAULT_HANDLER_MAPPER;

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
		this.handler = new ArrayList<>(handler);
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
		this.handler = new ArrayList<>(handler);
		this.onSave = onSave;
		this.file = null;
		yamlFile.loadFromString(yaml.replace("==: ", "clazz: "));
		if (!yamlFile.isConfigurationSection(root))
			throw new IllegalStateException(root + " is not configuration section");
	}

	public void open() {
		ConfigurationSectionEditor editor = new ConfigurationSectionEditor(this, null, null, p,
				yamlFile.getConfigurationSection(root), handler, null);
		editor.open();
	}

	public void addValidator(AbstractInputValidator<?> aiv) {
		validator.add(aiv);
	}

	public void setHandlerMapper(IHandlerMapper ihm) {
		handlerMapper = ihm;
	}

	public void addKeySuggester(IKeySuggester provider) {
		keySuggester.add(provider);
	}

	public void addValueSuggester(IValueSuggester provider) {
		valueSuggester.add(provider);
	}

	@SuppressWarnings("unchecked")
	public List<IKeySuggester> getKeySuggester() {
		return (List<IKeySuggester>) keySuggester.clone();
	}

	@SuppressWarnings("unchecked")
	public List<IValueSuggester> getValueSuggester() {
		return (List<IValueSuggester>) valueSuggester.clone();
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

	public void setKeyValidator(IKeyValidator ikv) {
		this.keyValidator = ikv;
	}

	public IKeyValidator getKeyValidator() {
		return keyValidator;
	}

	public boolean hasKeyValidator() {
		return keyValidator != null;
	}

	@SuppressWarnings("unchecked")
	public Set<IValueEditorListener> getListener() {
		return (Set<IValueEditorListener>) listener.clone();
	}

	@SuppressWarnings("nls")
	public IValueHandler<?> getHandler(ConfigurationSectionEditor where, String key) {
		IValueHandler<?> ivh = handlerMapper.getHandler(where, key);
		if (ivh != null)
			return ivh;
		ILibrary.getInstance().getLogger().severe(
				"No handler found for " + where.getCurrent().get(key).getClass() + ": " + where.getCurrent().get(key));
		return null;
	}

	@SuppressWarnings("nls")
	public IValueHandler<?> getListHandler(ListEditor where, Object o) {
		if (o == null)
			throw new IllegalArgumentException("value null");
		IValueHandler<?> ivh = handlerMapper.getListHandler(where, o);
		if (ivh != null)
			return ivh;
		ILibrary.getInstance().getLogger().severe("No handler found for " + o.getClass() + ": " + o);
		return null;
	}

	public Consumer<YamlEditor> getOnSave() {
		return onSave;
	}

	public YamlFile getYamlFile() {
		return yamlFile;
	}

	public EditorSettings getSettings() {
		return settings;
	}

	@SuppressWarnings("unchecked")
	public List<IValueHandler<?>> getValHandler() {
		return (List<IValueHandler<?>>) handler.clone();
	}

	public IListEditorListener getListTypeValidator() {
		return listEditorListener;
	}

	public void setListTypeValidator(IListEditorListener listEditorListener) {
		this.listEditorListener = listEditorListener;
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
