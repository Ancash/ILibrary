package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.EditorSettings;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;

public abstract class ValueEditor<T> extends IGUI {

	protected final EditorSettings settings;
	protected final Supplier<T> valSup;
	protected Runnable onBack;
	protected final ValueEditor<?> parent;
	protected final YamlEditor yeditor;
	protected final String key;

	public ValueEditor(UUID id, String title, int size, ValueEditor<?> parent, YamlEditor yeditor, String key,
			Supplier<T> valSup, Runnable onBack) {
		super(id, size, title);
		this.settings = yeditor.getSettings();
		this.key = key;
		this.yeditor = yeditor;
		this.onBack = onBack;
		this.parent = parent;
		this.valSup = valSup;
		for (int i = 0; i < getSize(); i++)
			setItem(settings.getBackgroundItem(), i);
		if (onBack != null)
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), getSize() - 5,
					(a, b, c, top) -> Lambda.execIf(top, this::back)));
		open();
	}

	public ConfigurationSectionEditor getClosesConfigurationSectionEditor() {
		if (this instanceof ConfigurationSectionEditor)
			return (ConfigurationSectionEditor) this;
		if (!hasParent())
			return null;
		return getParent().getClosesConfigurationSectionEditor();
	}

	public String getKey() {
		return key;
	}

	public boolean hasKey() {
		return key != null;
	}

	public ValueEditor<?> getParent() {
		return parent;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	protected ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.REDSTONE_TORCH).setDisplayname(String.valueOf(valSup.get())).build();
	}

	protected void back() {
		closeAll();
		onBack.run();
	}

	public YamlEditor getYamlEditor() {
		return yeditor;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		IGUIManager.remove(id);
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}
}
