package de.ancash.minecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.minecraft.nbt.NBTItem;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings({ "unchecked", "deprecation" })
public class ItemStackFileUtil {

	private static final Map<String, BiConsumer<ItemStack, Object>> GET_CONSUMERS = new HashMap<>();

	static {
		GET_CONSUMERS.put("amount", (item, amount) -> item.setAmount((int) amount));
		GET_CONSUMERS.put("texture", (item, texture) -> ItemStackUtils.setTexture(item, (String) texture));

		GET_CONSUMERS.put("meta.displayname", (item, name) -> ItemStackUtils.setDisplayname(item, (String) name));
		GET_CONSUMERS
				.put("meta.lore",
						(item, lore) -> ItemStackUtils.setLore(((List<String>) lore).stream()
								.map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()),
								item));
		GET_CONSUMERS.put("meta.enchantments",
				(item, enchantments) -> ((List<String>) enchantments)
						.forEach(ench -> item.addUnsafeEnchantment(Enchantment.getByName(ench.split(":")[0]),
								Integer.valueOf(ench.split(":")[1]))));
		GET_CONSUMERS.put("meta.flags", (item, flags) -> ((List<String>) flags).stream().map(ItemFlag::valueOf)
				.forEach(flag -> ItemStackUtils.addItemFlag(item, flag)));
	}

	public static ItemStack getItemStack(FileConfiguration fc, String path) {
		Optional<XMaterial> type = XMaterial.matchXMaterial(fc.getString(path + ".type"));
		if (!type.isPresent()) {
//			System.err.println("Invalid item type at " + path + ": " + fc.getString(path + ".type"));
			return XMaterial.AIR.parseItem();
		}
		ItemStack item = type.get().parseItem();
		if (type.get().equals(XMaterial.AIR))
			return item;
		ConfigurationSection cs = fc.getConfigurationSection(path);
		if (cs.contains("custom-model-data")) {
			ItemMeta m = item.getItemMeta();
			m.setCustomModelData(cs.getInt("custom-model-data"));
			item.setItemMeta(m);
		}

		for (String prop : cs.getKeys(true)) {
			if (cs.isConfigurationSection(prop))
				continue;
			if ("type".equals(prop))
				continue;
			if ("custom-model-data".equals(prop))
				continue;
			if (prop.startsWith("meta.nbt"))
				continue;
			if (!GET_CONSUMERS.containsKey(prop)) {
				continue;
			}
			if (cs.isString(prop))
				GET_CONSUMERS.get(prop).accept(item, cs.getString(prop));
			else if (cs.isBoolean(prop))
				GET_CONSUMERS.get(prop).accept(item, cs.getBoolean(prop));
			else if (cs.isInt(prop))
				GET_CONSUMERS.get(prop).accept(item, cs.getInt(prop));
			else if (cs.isList(prop))
				GET_CONSUMERS.get(prop).accept(item, cs.getList(prop));
		}
		if (cs.isConfigurationSection("meta.nbt")) {
			NBTItem nbt = new NBTItem(item);
			cs = cs.getConfigurationSection("meta.nbt");
			ConfigurationSection nbtType = null;
			if (cs.isConfigurationSection("long")) {
				nbtType = cs.getConfigurationSection("long");
				for (String key : nbtType.getKeys(true))
					nbt.setLong(key, nbtType.getLong(key));
			}
			if (cs.isConfigurationSection("integer")) {
				nbtType = cs.getConfigurationSection("integer");
				for (String key : nbtType.getKeys(true))
					nbt.setInteger(key, nbtType.getInt(key));
			}
			if (cs.isConfigurationSection("double")) {
				nbtType = cs.getConfigurationSection("double");
				for (String key : nbtType.getKeys(true))
					nbt.setDouble(key, nbtType.getDouble(key));
			}
			if (cs.isConfigurationSection("string")) {
				nbtType = cs.getConfigurationSection("string");
				for (String key : nbtType.getKeys(true))
					nbt.setString(key, nbtType.getString(key));
			}
			if (cs.isConfigurationSection("boolean")) {
				nbtType = cs.getConfigurationSection("boolean");
				for (String key : nbtType.getKeys(true))
					nbt.setBoolean(key, nbtType.getBoolean(key));
			}
			item = nbt.getItem();
		}
		return item;
	}

}