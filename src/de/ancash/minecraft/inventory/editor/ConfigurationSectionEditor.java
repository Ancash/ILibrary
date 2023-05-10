package de.ancash.minecraft.inventory.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.lambda.Lambda;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.handler.BooleanHandler;
import de.ancash.minecraft.inventory.editor.handler.ConfigurationSectionHandler;
import de.ancash.minecraft.inventory.editor.handler.DoubleHandler;
import de.ancash.minecraft.inventory.editor.handler.IValueHandler;
import de.ancash.minecraft.inventory.editor.handler.LongHandler;
import de.ancash.minecraft.inventory.editor.handler.StringHandler;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class ConfigurationSectionEditor extends ValueEditor<ConfigurationSection> {

	protected final YamlFileEditor editor;
	protected final ConfigurationSection root;
	protected ConfigurationSection current;
	protected Runnable onSave;
	protected final List<String> keys = new ArrayList<>();
	protected int keysPage;
	protected final Set<IValueHandler<?>> handler;
	protected final Map<String, IValueHandler<?>> mappedTypes = new HashMap<String, IValueHandler<?>>();
	protected boolean finishedConstructor = false;
	protected final Runnable onDelete;

	public ConfigurationSectionEditor(YamlFileEditor editor, Player player, ConfigurationSection root,
			ConfigurationSection current, Runnable onDelete) {
		this(editor, player, root, current, YamlFileEditor.DEFAULT_VALUE_HANDLER, onDelete);
	}

	public ConfigurationSectionEditor(YamlFileEditor editor, Player player, ConfigurationSection root,
			ConfigurationSection current, Set<IValueHandler<?>> handler, Runnable onDelete) {
		super(player.getUniqueId(), YamlFileEditor.createTitle(root, current), 54, editor.settings, null, null);
		finishedConstructor = true;
		this.onDelete = onDelete;
		this.handler = handler;
		this.root = root;
		this.current = current;
		this.editor = editor;
		open();
	}

	public void addRootBackItem(Runnable r) {
		onBack = r;
	}

	public YamlFileEditor getFile() {
		return editor;
	}

	public IValueHandler<?> getHandler(String key) {
		return mappedTypes.get(key);
	}

	@Override
	public void open() {
		if (!finishedConstructor)
			return;
		if (!root.getCurrentPath().isEmpty() && !current.getCurrentPath().startsWith(root.getCurrentPath())) {
			return;
		}
		loadConfiguration();
		mapTypes();
		loadPage();
		super.open();
	}

	protected void loadOptions() {
		addInventoryItem(new InventoryItem(this, settings.addStringItem(), 46,
				(a, b, c, top) -> Lambda.execIf(top, () -> createKey(StringHandler.INSTANCE,
						XMaterial.matchXMaterial(settings.addStringItem()).parseItem()))));
		addInventoryItem(new InventoryItem(this, settings.addLongItem(), 47, (a, b, c, top) -> Lambda.execIf(top,
				() -> createKey(LongHandler.INSTANCE, XMaterial.matchXMaterial(settings.addLongItem()).parseItem()))));
		addInventoryItem(new InventoryItem(this, settings.addDoubleItem(), 48,
				(a, b, c, top) -> Lambda.execIf(top, () -> createKey(DoubleHandler.INSTANCE,
						XMaterial.matchXMaterial(settings.addDoubleItem()).parseItem()))));
		addInventoryItem(new InventoryItem(this, settings.addBooleanItem(), 49,
				(a, b, c, top) -> Lambda.execIf(top, () -> createKey(BooleanHandler.INSTANCE,
						XMaterial.matchXMaterial(settings.addBooleanItem()).parseItem()))));
		addInventoryItem(new InventoryItem(this, settings.addConfigurationSectionItem(), 50,
				(a, b, c, top) -> Lambda.execIf(top, () -> createKey(ConfigurationSectionHandler.INSTANCE,
						XMaterial.matchXMaterial(settings.addConfigurationSectionItem()).parseItem()))));
		if (onDelete != null)
			addInventoryItem(
					new InventoryItem(this, settings.deleteItem(), 51, (a, b, c, top) -> Lambda.execIf(top, () -> {
						onDelete.run();
						super.back();
					})));
		addInventoryItem(new InventoryItem(this, settings.saveItem(), 52, (a, b, c, top) -> Lambda.execIf(top, () -> {
			editor.onSave.accept(editor);
			closeAll();
		})));
	}

	protected void createKey(IValueHandler<?> type, ItemStack item) {
		closeAll();
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()));
		sig.setTitle("Create " + type.getClazz().getSimpleName());
		sig.setLeft(item);
		sig.setText("key");
		sig.onComplete(key -> {
			current.set(key, type.defaultValue());
			mapTypes();
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> open(), keysPage);
		});
		IGUIManager.remove(getId());
		sig.open();

	}

	public void newInventory() {
		newInventory(YamlFileEditor.createTitle(root, current), getSize());
	}

	@SuppressWarnings("nls")
	protected void mapTypes() {
		mappedTypes.clear();
		for (String key : getCurrent().getKeys(false)) {
			IValueHandler<?> ivh = null;
			for (IValueHandler<?> h : handler) {
				if (h.isValid(getCurrent(), key)) {
					if (ivh != null)
						throw new IllegalStateException(
								"multiple matches for " + key + " in " + getCurrent().getCurrentPath() + ": "
										+ ivh.getClass().getCanonicalName() + " & " + h.getClass().getCanonicalName());
					else
						ivh = h;
				}
			}
			if (ivh != null)
				mappedTypes.put(key, ivh);
			else
				throw new IllegalStateException("unknown type at "
						+ String.join(".", getCurrent().getCurrentPath(), key) + ": " + getCurrent().get(key));
		}
	}

	protected void loadConfiguration() {
		newInventory(getTitle(), getSize());
		keysPage = 0;
		keys.clear();
		keys.addAll(getCurrent().getKeys(false));
	}

	protected void loadPage() {
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
			IValueHandler<?> handler = mappedTypes.get(key);
			ItemStack item = handler.getEditItem(this, key);
			addInventoryItem(new InventoryItem(this, item, pos++,
					(a, b, c, top) -> Lambda.execIf(top, () -> handler.edit(this, key))));
		}
		if (onBack != null)
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), getSize() - 9,
					(a, b, c, top) -> Lambda.execIf(top, onBack)));
		if (hasPrevPage())
			addInventoryItem(new InventoryItem(this, settings.getPrevItem(), getSize() - 2,
					(a, b, c, top) -> Lambda.execIf(top, this::prevPage)));
		if (hasNextPage())
			addInventoryItem(new InventoryItem(this, settings.getNextItem(), getSize() - 1,
					(a, b, c, top) -> Lambda.execIf(top, this::nextPage)));
		loadOptions();
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

	public Set<IValueHandler<?>> getValueHandler() {
		return handler;
	}
}
