package de.ancash.minecraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.ancash.datastructures.maps.CompactMap;
import de.ancash.minecraft.nbt.NBTItem;

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
	
	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack a, ItemStack b) {
		a = legacyToNormal(a);
		b = legacyToNormal(b);
		boolean aNull = a == null || a.getType() == Material.AIR;
		boolean bNull = b == null || b.getType() == Material.AIR;		
		
		if(a == null && b == null) {
			return false;
		}
			
		if(aNull != bNull) {
			return false;
		}
		
		if(aNull == true) {
			return false;
		}
		
		if(!a.getType().equals(b.getType()) && b.getType().getId() != 0) {
			return false;
		}
		if(!matchesMeta(a, b)) {
			return false;
		}
		
		if(!matchesNBT(a, b)) {
			return false;
		}
		
		boolean matches = false;
		if(!a.getType().equals(Material.valueOf("SKULL_ITEM"))) {
			String one = a.toString().split("\\{")[1].split(" x")[0];
			String two = b.toString().split("\\{")[1].split(" x")[0];
			if(!one.equals(two)) {
				if(!(one.split("_").length == two.split("_").length && one.split("_").length > 1 && one.split("_")[0].equals(two.split("_")[0]))) {
					return false;
				}
			}
			if(one.equals(two)) matches = true;
		} else {
			SkullMeta aM = (SkullMeta) a.getItemMeta();
			SkullMeta bM = (SkullMeta) b.getItemMeta();
			if(aM.getOwner() == null && bM.getOwner() != null && !bM.getOwner().equals("Ancash")) 
				return false;
			
			if(aM.getOwner() != null && bM.getOwner() == null && !aM.getOwner().equals("Ancash")) 
				return false;
			
			if(aM.getOwner() != null && !aM.getOwner().equals(bM.getOwner()))  
				return false;
			
			try {
				String aT = getTexure(a);
				String bT = getTexure(b);
				if(aT != null && !aT.equals(bT)) return false;
				if(bT != null && !aT.equals(aT)) return false;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			matches = true;
		}
		if(a.getData().getData() != -1 
				&& b.getData().getData() != -1 
				&& a.getData().getData() != b.getData().getData()
				&& !matches) {
			return false;
		}
		return true;
	}
	
	public static boolean matchesNBT(ItemStack a, ItemStack b) {
		NBTItem aNbt = new NBTItem(a);
		NBTItem bNbt = new NBTItem(b);
		
		if(!aNbt.getKeys().equals(bNbt.getKeys())) {
			if(!(aNbt.getKeys().size() - 2 == bNbt.getKeys().size() ||
					aNbt.getKeys().size() - 1 == bNbt.getKeys().size() ||
					aNbt.getKeys().size() + 2 == bNbt.getKeys().size() ||
					aNbt.getKeys().size() + 1 == bNbt.getKeys().size())) {
				return false;
			}
		}
		for(String key : aNbt.getKeys()) {
			if(key.equals("meta-type") || key.equals("Damage")) continue;
			try {
				Object aOb = aNbt.getObject(key, Object.class);
				Object bOb = bNbt.getObject(key, Object.class);
				if(aOb == null && bOb == null) continue;
				if(aOb.equals(bOb)) return false;
			} catch(Exception exc) {}
		}
		return true;
	}
	
	public static boolean matchesMeta(ItemStack a, ItemStack b) {
		boolean aHas = a.getItemMeta() == null;
		boolean bHas = b.getItemMeta() == null;
		if(aHas != bHas) return false;
		if(a.getItemMeta() == null || b.getItemMeta() == null) return true;
		ItemMeta aM = a.getItemMeta();
		ItemMeta bM = b.getItemMeta();
		if(aM.getLore() == null && bM.getLore() != null) return false;
		if(aM.getLore() != null && bM.getLore() == null) return false;
		if(aM.getLore() != null && !aM.getLore().equals(bM.getLore())) return false;
		if(!aM.getEnchants().equals(bM.getEnchants())) return false;
		if(aM.getDisplayName() != null && bM.getDisplayName() == null) return false;
		if(aM.getDisplayName() == null && bM.getDisplayName() != null) return false;
		if(aM.getDisplayName() != null && !aM.getDisplayName().equals(bM.getDisplayName())) return false;
		if(!aM.getItemFlags().equals(bM.getItemFlags())) return false;
		return true;
	}
	
	public static ItemStack replacePlaceholder(ItemStack is, CompactMap<String, String> placeholder) {
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
	
	@SuppressWarnings("deprecation")
	public static ItemStack get(FileConfiguration fc, String path) {
		if(fc.getItemStack(path) != null) {
			ItemStack is = fc.getItemStack(path);
			if(!is.getType().equals(Material.SKULL_ITEM) || is.getData().getData() == 3) {
				return is;
			}
			if(fc.getString(path + "-texture") != null) is = setTexture(is, fc.getString(path + "-texture"));
			return is;
		}
		if(fc.getString(path + ".type") == null) return null; 
		ItemStack is = new ItemStack(Material.valueOf(fc.getString(path + ".type")), 1, (short) fc.getInt(path + ".meta.data"));
		ItemMeta im = is.getItemMeta();
		
		if(fc.getString(path + ".meta.displayname") != null) im.setDisplayName(fc.getString(path + ".meta.displayname").replace("&", "§"));
		
		List<String> lore = new ArrayList<String>();
		fc.getStringList(path + ".meta.lore").forEach(str -> lore.add(str.replace("&", "§")));
		if(lore != null) im.setLore(lore);
		
		List<String> flags = fc.getStringList(path + ".meta.flags");
		for(String flag : flags) 
			im.addItemFlags(ItemFlag.valueOf(flag));
		
		is.setItemMeta(im);
		
		if(is.getType().equals(Material.valueOf("SKULL_ITEM")) && fc.getString(path + ".meta.texture") != null)
			is = setTexture(is, fc.getString(path + ".meta.texture"));
		
		List<String> enchs = fc.getStringList(path + ".meta.enchantments");
		for(String ench : enchs) 
			is.addUnsafeEnchantment(Enchantment.getByName(ench.split(":")[0]), Integer.valueOf(ench.split(":")[1]));
		
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static void set(FileConfiguration fc, String path, ItemStack is) {
		ItemStack result = is.clone();
		if(result.getType().equals(Material.SKULL_ITEM) && result.getData().getData() == 3) {
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
		}
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
