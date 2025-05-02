package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;

import de.ancash.ILibrary;
import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemStackUtils;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.EditorSettings;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.KeySuggestion;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class ConfigurationSectionEditor extends ValueEditor<ConfigurationSection> {

	protected final ConfigurationSection root;
	protected ConfigurationSection current;
	protected int addPos = 0;
	protected Runnable onSave;
	protected final List<String> keys = new ArrayList<>();
	protected int keysPage;
	protected final List<IValueHandler<?>> handler;
	protected final Map<String, IValueHandler<?>> mappedHandler = new HashMap<String, IValueHandler<?>>();
	protected final List<KeySuggestion> suggestions = new ArrayList<>();
	protected int sugPos = 0;
	protected boolean finishedConstructor = false;
	protected final Runnable onDelete;

	public ConfigurationSectionEditor(YamlEditor editor, ValueEditor<?> parent, String key, Player player, ConfigurationSection current,
			Runnable onDelete) {
		this(editor, parent, key, player, current, YamlEditor.getDefaultHandler(), onDelete);
	}

	public ConfigurationSectionEditor(YamlEditor editor, ValueEditor<?> parent, String key, Player player, ConfigurationSection current,
			List<IValueHandler<?>> handler, Runnable onDelete) {
		super(player.getUniqueId(), YamlEditor.createTitle(editor.getRoot(), current), 54, parent, editor, key, null, null);
		finishedConstructor = true;
		this.onDelete = onDelete;
		this.handler = Collections.unmodifiableList(handler);
		this.root = editor.getRoot();
		this.current = current;
		open();
	}

	@Override
	public int hashCode() {
		return current.getCurrentPath().hashCode();
	}

	public void addRootBackItem(Runnable r) {
		onBack = r;
	}

	public IValueHandler<?> getHandler(String key) {
		return mappedHandler.get(key);
	}

	@Override
	public void open() {
		if (!finishedConstructor)
			return;
		if (!root.getCurrentPath().isEmpty() && !current.getCurrentPath().startsWith(root.getCurrentPath())) {
			return;
		}
		newInventory(getTitle(), getSize());
		loadPage();
		loadOptions();
		super.open();
	}

	protected void loadOptions() {
		if (onDelete != null)
			addInventoryItem(new InventoryItem(this, settings.deleteItem(), 51, (a, b, c, top) -> Lambda.execIf(top, () -> {
				onDelete.run();
				super.back();
			})));
		addInventoryItem(new InventoryItem(this, settings.saveItem(), 52, (a, b, c, top) -> Lambda.execIf(top, () -> {
			saveListElement(current);
			yeditor.getOnSave().accept(yeditor);
			closeAll();
		})));
		addAddItem();
		loadSuggestions();
		addSuggestionsItem();
	}

	@Override
	protected void saveListElement(Object val) {
		if (parent != null && (parent instanceof ListEditor || parent instanceof ConfigurationSectionEditor))
			parent.saveListElement(current);
	}

	@SuppressWarnings("nls")
	protected void addAddItem() {
		StringBuilder builder = new StringBuilder();
		builder.append("§eRight click to select type").append("\n").append("§eLeft click to add property").append("\n");
		for (int i = 0; i < handler.size(); i++) {
			IValueHandler<?> ivh = handler.get(i);
			ItemStack add = ivh.getAddItem();
			if (add == null)
				continue;
			if (i == addPos) {
				builder.append("§a");
			} else
				builder.append("§f");
			builder.append("Add " + ivh.getClazz().getSimpleName());
			builder.append("\n");
		}

		addInventoryItem(new InventoryItem(this, ItemStackUtils.setLore(settings.addItem(), builder.toString().split("\n")), 46,
				(slot, shift, action, top) -> {
					if (!top)
						return;
					switch (action) {
					case PICKUP_ALL:
						createKey(handler.get(addPos));
						break;
					case PICKUP_HALF:
						nextAddOption();
						addAddItem();
						break;
					default:
						break;
					}
				}));
	}

	@Override
	protected void useSuggestion(ValueSuggestion<ConfigurationSection> sugg) {
		throw new UnsupportedOperationException();
	}

	protected void nextAddOption() {
		addPos = (addPos + 1) % handler.size();
		if (handler.get(addPos).getAddItem() == null)
			nextAddOption();
	}

	protected void loadSuggestions() {
		suggestions.clear();
		yeditor.getKeySuggester().stream().map(k -> k.getKeySuggestions(this)).filter(s -> s != null && !s.isEmpty()).flatMap(Set::stream)
				.sorted((a, b) -> a.getKey().compareTo(b.getKey())).forEach(suggestions::add);
		for (int i = 0; i < suggestions.size() - 1; i++)
			if (suggestions.get(i).getKey().equals(suggestions.get(i + 1).getKey()))
				throw new IllegalStateException("duplicate key suggestion: " + suggestions.get(i).getKey());
	}

	@SuppressWarnings("nls")
	protected void addSuggestionsItem() {
		StringBuilder lore = new StringBuilder();
		lore.append("§eRight click to select suggestion").append("\n").append("§eLeft click to add suggestion").append("\n").append("§7Suggestions:");

		for (int i = 0; i < suggestions.size(); i++) {
			KeySuggestion cskc = suggestions.get(i);
			lore.append("\n");

			if (i == sugPos)
				lore.append("§a");
			else
				lore.append("§f");
			if (current.contains(cskc.getKey()))
				lore.append("§c");
			lore.append(String.format("%s (%s)", cskc.getKey(), cskc.getName()));
		}
		addInventoryItem(new InventoryItem(this, ItemStackUtils.setLore(settings.suggestionsItem(), lore.toString().split("\n")), 47,
				(slot, shift, action, top) -> {
					if (!top)
						return;
					if (suggestions.isEmpty())
						return;
					switch (action) {
					case PICKUP_ALL:
						createSuggestion(suggestions.get(sugPos));
						open();
						break;
					case PICKUP_HALF:
						nextSuggestionsOption();
						addSuggestionsItem();
						break;
					default:
						break;
					}
				}));
	}

	protected void nextSuggestionsOption() {
		nextSuggestionsOption(0);
	}

	protected void nextSuggestionsOption(int cnt) {
		if (suggestions.isEmpty() || cnt > suggestions.size())
			return;
		sugPos = (sugPos + 1) % suggestions.size();
		if (current.contains(suggestions.get(sugPos).getKey()))
			nextSuggestionsOption(cnt + 1);
	}

	protected void createSuggestion(KeySuggestion cskc) {
		if (current.contains(cskc.getKey()))
			return;
		cskc.createKey(this);
	}

	@SuppressWarnings("nls")
	protected void createKey(IValueHandler<?> type) {
		closeAll();
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()));
		AtomicReference<String> res = new AtomicReference<>();
		sig.setTitle("Create " + type.getClazz().getSimpleName());
		sig.setLeft(type.getAddItem());
		sig.setText("key");
		sig.onComplete(key -> {
			type.setDefaultValue(current, res.get());
			mapHandler();
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> open(), keysPage);
		});
		sig.isValid(in -> {
			if (!yeditor.hasKeyValidator())
				return Tuple.of(true, null);
			Duplet<String, String> dup = yeditor.getKeyValidator().validate(this, type, in);
			res.set(dup.getFirst());
			return Tuple.of(dup.getFirst() != null, dup.getSecond());
		});
		IGUIManager.remove(getId());
		sig.open();
	}

	public void newInventory() {
		newInventory(YamlEditor.createTitle(root, current), getSize());
	}

	@SuppressWarnings("nls")
	protected void mapHandler() {
		mappedHandler.clear();
		for (String key : getCurrent().getKeys(false)) {
			IValueHandler<?> ivh = yeditor.getHandler(this, key);
			if (ivh != null)
				mappedHandler.put(key, ivh);
			else
				throw new IllegalStateException(
						"unknown type at " + String.join(".", getCurrent().getCurrentPath(), key) + ": " + getCurrent().get(key));
		}
	}

	protected void loadPage() {
		keysPage = 0;
		keys.clear();
		keys.addAll(getCurrent().getKeys(false));
		mapHandler();

		for (int i = 0; i < getSize() - 9; i++) {
			setItem(null, i);
			removeInventoryItem(i);
		}
		for (int i = inv.getSize() - 9; i < inv.getSize(); i++)
			setItem(getSettings().getBackgroundItem(), i);
		int pos = 0;

		for (int k = keysPage * (getSize() - 9); k < (keysPage + 1) * (getSize() - 9); k++) {
			if (keys.size() <= k)
				break;
			String key = keys.get(k);
			IValueHandler<?> handler = mappedHandler.get(key);
			ItemStack item = handler.getEditItem(this, key);
			addInventoryItem(new InventoryItem(this, item, pos++, (a, b, c, top) -> Lambda.execIf(top, () -> handler.edit(this, key))));
		}
		if (onBack != null)
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), getSize() - 9, (a, b, c, top) -> Lambda.execIf(top, onBack)));
		if (hasPrevPage())
			addInventoryItem(new InventoryItem(this, settings.getPrevItem(), getSize() - 2, (a, b, c, top) -> Lambda.execIf(top, this::prevPage)));
		if (hasNextPage())
			addInventoryItem(new InventoryItem(this, settings.getNextItem(), getSize() - 1, (a, b, c, top) -> Lambda.execIf(top, this::nextPage)));
	}

	public void prevPage() {
		if (keysPage >= 1) {
			keysPage--;
			loadPage();
		}
	}

	public boolean hasPrevPage() {
		return keysPage >= 1;
	}

	public boolean hasNextPage() {
		return (keysPage + 1) * (getSize() - 9) < keys.size();
	}

	public void nextPage() {
		if ((keysPage + 1) * (getSize() - 9) < keys.size()) {
			keysPage++;
			loadPage();
		}
	}

	public void onSave(Runnable r) {
		this.onSave = r;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		IGUIManager.remove(getId());
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}

	public ConfigurationSection getCurrent() {
		return current;
	}

	@SuppressWarnings("nls")
	public void setCurrentConfigurationSection(ConfigurationSection section) {
		if (!section.getCurrentPath().contains(root.getCurrentPath()))
			throw new IllegalArgumentException("higher than root");
		current = section;
	}

	public EditorSettings getSettings() {
		return settings;
	}

	public ConfigurationSection getRoot() {
		return root;
	}

	public List<IValueHandler<?>> getValueHandler() {
		return handler;
	}
}
