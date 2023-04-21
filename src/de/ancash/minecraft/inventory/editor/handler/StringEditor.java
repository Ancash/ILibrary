package de.ancash.minecraft.inventory.editor.handler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.ConfigurationSectionEditor;
import de.ancash.minecraft.inventory.editor.YamlFileEditor;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class StringEditor extends ValueEditor {

	public StringEditor(ConfigurationSectionEditor editor, String key) {
		super(36, editor, key);
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(section.get(key))).build();
	}

	public void acceptInput() {
		StringInputGUI sig = new StringInputGUI(ILibrary.getInstance(), Bukkit.getPlayer(getId()), s -> {
			section.set(key, s);
			Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new StringEditor(editor, key), 1);
		});
		sig.setLeft(XMaterial.DIRT.parseItem());
		sig.setTitle(YamlFileEditor.cut(String.join(":",
				YamlFileEditor.createTitle(editor.getRoot(), editor.getCurrentConfigurationSection(), key),
				editor.getHandler(key).getClazz().getSimpleName()), openTick));
		sig.setText(editor.getCurrentConfigurationSection().get(key).toString());
		sig.isValid(str -> Tuple.of(str != null, null));
		closeAll();
		Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> sig.open(), 1);
	}
}
