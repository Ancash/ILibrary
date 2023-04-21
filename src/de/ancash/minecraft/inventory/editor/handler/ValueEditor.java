package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;

public abstract class ValueEditor extends IGUI {

	protected final ConfigurationSectionEditor editor;
	protected final String key;
	protected final ConfigurationSection section;

	public ValueEditor(int size, ConfigurationSectionEditor editor, String key) {
		super(editor.getId(), size,
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrentConfigurationSection(), key));
		for (int i = 0; i < getSize(); i++)
			setItem(editor.getSettings().getBackgroundItem(), i);
		addInventoryItem(new InventoryItem(this, editor.getSettings().getBackItem(), getSize() - 5,
				(a, b, c, top) -> Lambda.execIf(top, this::back)));
		this.key = key;
		this.editor = editor;
		this.section = editor.getCurrentConfigurationSection();
		open();
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.REDSTONE_TORCH).setDisplayname(String.valueOf(section.get(key))).build();
	}

	protected void back() {
		closeAll();
		editor.open();
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
		System.out.println(event.getAction());
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
