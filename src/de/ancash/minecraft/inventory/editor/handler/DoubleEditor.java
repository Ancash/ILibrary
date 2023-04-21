package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;
import de.ancash.minecraft.inventory.input.NumberInputGUI;

public class DoubleEditor extends ValueEditor {

	public DoubleEditor(ConfigurationSectionEditor editor, String key) {
		super(36, editor, key);
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(section.get(key))).build();
	}

	public void acceptInput() {
		NumberInputGUI<Double> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()),
				Double.class, s -> {
					section.set(key, s);
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new DoubleEditor(editor, key), 1);
				});
		nig.setLeft(XMaterial.DIRT.parseItem());
		nig.setTitle(YamlFileEditor.cut(String.join(":",
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrentConfigurationSection(), key),
				editor.getHandler(key).getClazz().getSimpleName()), openTick));
		nig.setText(editor.getCurrentConfigurationSection().get(key).toString());
		closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> nig.start(), 1);
	}
}
