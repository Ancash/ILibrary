package de.ancash.minecraft.inventory.editor;

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.editor.handler.IValueHandler;

public interface EditorSettings {

	public default ItemStack getBackgroundItem() {
		return XMaterial.BEDROCK.parseItem();
	}

	public default ItemStack getSectionItem() {
		return XMaterial.CHEST.parseItem();
	}

	public default ItemStack getBooleanItem() {
		return XMaterial.REDSTONE_TORCH.parseItem();
	}

	@SuppressWarnings("nls")
	public default ItemStack getKeyValueItem(ConfigurationSection section, String key, IValueHandler<?> type) {
		StringBuilder b = new StringBuilder();
		b.append("§7Key: ").append(key).append("\n").append("§7Type: ").append(type.getClazz().getSimpleName())
				.append("\n").append("§7Value:").append("\n");
		for (String s : type.valueToString(section, key).split("\n"))
			b.append("§f").append(s).append("\n");
		return new ItemBuilder(XMaterial.CHEST).setDisplayname(key)
				.setLore(b.toString().substring(0, b.length() - 1).split("\n")).build();
	}

	@SuppressWarnings("nls")
	public default ItemStack getBackItem() {
		return new ItemBuilder(XMaterial.ARROW).setDisplayname("Back").build();
	}

	@SuppressWarnings("nls")
	public default ItemStack getNextItem() {
		return new ItemBuilder(XMaterial.ARROW).setDisplayname("Next").build();
	}

	@SuppressWarnings("nls")
	public default ItemStack getPrevItem() {
		return new ItemBuilder(XMaterial.ARROW).setDisplayname("Prev").build();
	}
}
