package de.ancash.minecraft.inventory.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import de.ancash.lambda.Lambda;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.handler.IValueHandler;

public class ConfigurationSectionEditor extends IGUI {

	protected final YamlFileEditor editor;
	protected final YamlFileEditorSettings settings;
	protected final ConfigurationSection root;
	protected ConfigurationSection current;
	protected Runnable onSave;
	protected final List<String> keys = new ArrayList<>();
	protected int keysPage;
	protected final List<IValueHandler<?>> handler;
	protected final Map<String, IValueHandler<?>> mappedTypes = new HashMap<String, IValueHandler<?>>();

	public ConfigurationSectionEditor(YamlFileEditor editor, Player player, ConfigurationSection root,
			ConfigurationSection current) {
		this(editor, player, root, current, YamlFileEditor.DEFAULT_VALUE_HANDLER);
	}

	public ConfigurationSectionEditor(YamlFileEditor editor, Player player, ConfigurationSection root,
			ConfigurationSection current, List<IValueHandler<?>> handler) {
		super(player.getUniqueId(), 54, YamlFileEditor.createTitle(root, current));
		this.handler = handler;
		this.root = root;
		this.current = current;
		this.editor = editor;
		this.settings = editor.settings;
	}

	public IValueHandler<?> getHandler(String key) {
		return mappedTypes.get(key);
	}

	@Override
	public void open() {
		loadConfiguration();
		mapTypes();
		loadPage();
		super.open();
	}

	public void newInventory() {
		newInventory(YamlFileEditor.createTitle(root, current), getSize());
	}

	@SuppressWarnings("nls")
	protected void mapTypes() {
		mappedTypes.clear();
		for (String key : getCurrentConfigurationSection().getKeys(false)) {
			IValueHandler<?> ivh = null;
			for (IValueHandler<?> h : handler) {
				if (h.isValid(getCurrentConfigurationSection(), key)) {
					if (ivh != null)
						throw new IllegalStateException("multiple matches for " + key + " in "
								+ getCurrentConfigurationSection().getCurrentPath() + ": "
								+ ivh.getClass().getCanonicalName() + " & " + h.getClazz().getCanonicalName());
					else
						ivh = h;
				}
			}
			if (ivh != null)
				mappedTypes.put(key, ivh);
			else
				throw new IllegalStateException(
						"unknown type at " + String.join(".", getCurrentConfigurationSection().getCurrentPath(), key)
								+ ": " + getCurrentConfigurationSection().get(key));
		}
	}

	protected void loadConfiguration() {
		newInventory(getTitle(), getSize());
		for (int i = 0; i < inv.getSize() % 9; i++)
			setItem(getSettings().getBackgroundItem(), i * 9 + 4);
		for (int i = inv.getSize() - 9; i < inv.getSize(); i++)
			setItem(getSettings().getBackgroundItem(), i);
		keysPage = 0;
		keys.clear();
		keys.addAll(getCurrentConfigurationSection().getKeys(false));
	}

	public void loadPage() {
		for (int a = 0; a < inv.getSize() % 9 - 1; a++) {
			for (int b = 0; b < 9; b++) {
				if (b == 4)
					continue;
				setItem(null, a * 9 + b);
			}
		}
		int pos = 0;

		for (int k = keysPage * (getSize() - 9); k < (keysPage + 1) * (getSize() - 9); k++) {
			if (keys.size() <= k)
				break;
			String key = keys.get(k);
			IValueHandler<?> handler = mappedTypes.get(key);
			ItemStack item = handler.getEditItem(this, key);
			addInventoryItem(new InventoryItem(this, item, pos++,
					(a, b, c, top) -> Lambda.execIf(top, () -> handler.edit(this, key))));
		}
		if (current.getParent() != null && !current.equals(root))
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), 45,
					(a, b, c, top) -> Lambda.execIf(top, this::back)));
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

	public ConfigurationSection getCurrentConfigurationSection() {
		return current;
	}

	@SuppressWarnings("nls")
	public void setCurrentConfigurationSection(ConfigurationSection section) {
		if (!section.getCurrentPath().contains(root.getCurrentPath()))
			throw new IllegalArgumentException("higher than root");
		current = section;
	}

	public YamlFileEditorSettings getSettings() {
		return settings;
	}

	public ConfigurationSection getRoot() {
		return root;
	}

	public void back() {
		if (!current.equals(root))
			current = current.getParent();
		newInventory();
		open();
	}

	public List<IValueHandler<?>> getAllHandler() {
		return handler;
	}
}
