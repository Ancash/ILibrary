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

public class LongEditor extends ValueEditor {

	public LongEditor(ConfigurationSectionEditor editor, String key) {
		super(36, editor, key);
		addInventoryItem(
				new InventoryItem(this, getEditorItem(), 13, (a, b, c, top) -> Lambda.execIf(top, this::acceptInput)));
	}

	public ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname(String.valueOf(section.get(key))).build();
	}

	@SuppressWarnings("nls")
	public void acceptInput() {
		NumberInputGUI<Long> nig = new NumberInputGUI<>(ILibrary.getInstance(), Bukkit.getPlayer(getId()), Long.class,
				s -> {
					section.set(key, s);
					Bukkit.getScheduler().runTaskLater(ILibrary.getInstance(), () -> new LongEditor(editor, key), 1);
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
