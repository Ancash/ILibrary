package de.ancash.minecraft;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemStackUtils {

	private static final Inventory inv = Bukkit.createInventory(null, 9);
	
	public static ItemStack legacyToNormal(ItemStack legacy) {
		synchronized (inv) {
			inv.setItem(0, legacy);
			legacy = inv.getItem(0);
			inv.setItem(0, null);
			return legacy;
		}
	}
	
	public static ItemStack replacePlaceholder(ItemStack is, Map<String, String> placeholder) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		for(String str : im.getLore()) {
			for(String place : placeholder.keySet())
				if(str.contains(place)) {
					str = str.replace(place, placeholder.get(place) == null ? "" : placeholder.get(place));
				}
			if(str.contains("\n")) {
				for(String s : str.split("\n"))
					lore.add(s);
			} else {
				lore.add(str);
			}
		}
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack setDisplayname(ItemStack is, String str) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(str);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack setLore(ItemStack is, String...str) {
		return setLore(Arrays.asList(str), is);
	}
	
	public static ItemStack setLore(List<String> lore, ItemStack is) {
		ItemMeta im = is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack removeLine(String hasToContain, ItemStack is) {
		ItemMeta im = is.getItemMeta();
		List<String> newLore = new ArrayList<String>();
		for(String str : im.getLore()) {
			if(!str.contains(hasToContain)) newLore.add(str);
		}
		im.setLore(newLore);
		is.setItemMeta(im);
		return is;
	}
	
	@Deprecated
	public static ItemStack get(FileConfiguration fc, String path) throws IOException {
		if(fc.isString(path))
			try {
				return SerializableItemStack.fromBase64(fc.getString(path)).restore();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return null;
			}
		if(fc.getItemStack(path) != null) {
			ItemStack is = fc.getItemStack(path);
			if(!is.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()) || is.getData().getData() == 3) {
				return is;
			}
			if(fc.getString(path + "-texture") != null) is = setTexture(is, fc.getString(path + "-texture"));
			return is;
		}
		if(fc.getString(path + ".type") == null) return null; 
		ItemStack is = XMaterial.matchXMaterial(fc.getString(path + ".type") + ": " + fc.getInt(path + ".meta.data")).get().parseItem().clone();
		ItemMeta im = is.getItemMeta();
		
		if(fc.getString(path + ".meta.displayname") != null) im.setDisplayName(fc.getString(path + ".meta.displayname").replace("&", "§"));
		
		List<String> lore = new ArrayList<String>();
		fc.getStringList(path + ".meta.lore").forEach(str -> lore.add(str.replace("&", "§")));
		if(lore != null) im.setLore(lore);
		
		List<String> flags = fc.getStringList(path + ".meta.flags");
		for(String flag : flags) 
			im.addItemFlags(ItemFlag.valueOf(flag));
		
		is.setItemMeta(im);
		
		if(is.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()) && fc.getString(path + ".meta.texture") != null)
			is = setTexture(is, fc.getString(path + ".meta.texture"));
		
		List<String> enchs = fc.getStringList(path + ".meta.enchantments");
		for(String ench : enchs) 
			is.addUnsafeEnchantment(Enchantment.getByName(ench.split(":")[0]), Integer.valueOf(ench.split(":")[1]));
		
		return is;
	}
	
	public static void set(FileConfiguration fc, String path, ItemStack is) {
		fc.set(path, new SerializableItemStack(is).asBase64());
		/*ItemStack result = is.clone();
		if(result.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()) && result.getData().getData() == 3) {
			String texture = null;
			try {
				texture = getTexure(result);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException e) {}
			if(texture != null) {
				SkullMeta sm = (SkullMeta) result.getItemMeta();
				sm.setOwner("Ancash");
				result.setItemMeta(sm);
				fc.set(path, result);
				fc.set(path + "-texture", texture);
			}
		} else {
			fc.set(path, result);
		}*/
	}
	
	public static String getTexure(ItemStack is) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String texture = null;
		
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		Field profileField = sm.getClass().getDeclaredField("profile");
		profileField.setAccessible(true);
		GameProfile profile = (GameProfile) profileField.get(sm);
		Collection<Property> textures = profile.getProperties().get("textures");
		for(Property p : textures) {
			texture = p.getValue();
		}
		return texture;
	}
	
	public static ItemStack setTexture(ItemStack is, String texture) {
		SkullMeta hm = (SkullMeta) is.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(hm, profile);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		is.setItemMeta(hm);
		return is;
	}
}
