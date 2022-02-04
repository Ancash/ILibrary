package de.ancash.minecraft;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.ancash.minecraft.nbt.utils.MinecraftVersion;

public class ItemStackUtils {

	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitOut = new BukkitObjectOutputStream(byteOut);
            bukkitOut.writeInt(items.length);
            for (int i = 0; i < items.length; i++)
                bukkitOut.writeObject(items[i]);
            bukkitOut.close();
            return Base64.getEncoder().encodeToString(byteOut.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(byteIn);
            ItemStack[] items = new ItemStack[bukkitIn.readInt()];
    
            for (int i = 0; i < items.length; i++)
            	items[i] = (ItemStack) bukkitIn.readObject();
            
            bukkitIn.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
	
	public static ItemStack replacePlaceholder(ItemStack is, Map<String, String> placeholder) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		for(String str : im.getLore()) {
			for(String place : placeholder.keySet())
				if(str.contains(place))
					str = str.replace(place, placeholder.get(place) == null ? "" : placeholder.get(place));
			
			if(str.contains("\n"))
				for(String s : str.split("\n"))
					lore.add(s);
			else
				lore.add(str);
		}
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack getItemStack(FileConfiguration fc, String path) {
		ItemStack item = fc.getItemStack(path).clone();
		if(!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1))
			if(fc.getString(path + "-texture") != null)
				item = setProfileName(item, null);		
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public static void setItemStack(FileConfiguration fc, String path, ItemStack item) {
		item = item.clone();
		if(item.getItemMeta() instanceof SkullMeta) {
			
			
			if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1)) {
				fc.set(path, item);
			} else {
				String txt = null;
				try {
					txt = ItemStackUtils.getTexure(item);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					
				}
				SkullMeta sm = (SkullMeta) item.getItemMeta();
				if(txt == null) {
					txt = sm.getOwner();
				} else {
					item = setProfileName(item, txt);
				}
				fc.set(path, item);
				fc.set(path + "-texture", txt);
			}
		} else {
			fc.set(path, item);
		}
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
		for(String str : im.getLore())
			if(!str.contains(hasToContain)) newLore.add(str);
		im.setLore(newLore);
		is.setItemMeta(im);
		return is;
	}
	
	@Deprecated
	public static ItemStack get(FileConfiguration fc, String path) throws IOException {
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
	
	@Deprecated
	public static void set(FileConfiguration fc, String path, ItemStack is) {
		setItemStack(fc, path, is);
	}
	
	public static ItemStack setProfileName(ItemStack is, String name) {
		SkullMeta hm = (SkullMeta) is.getItemMeta().clone();
		try {
			GameProfile profile = null;
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			profile = (GameProfile) field.get(hm);
			Field nameField = profile.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(profile, name);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		is.setItemMeta(hm);
		return is;
	}
	
	public static String getTexure(ItemStack is) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String texture = null;
		
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		Field profileField = sm.getClass().getDeclaredField("profile");
		profileField.setAccessible(true);
		GameProfile profile = (GameProfile) profileField.get(sm);
		Collection<Property> textures = profile.getProperties().get("textures");
		for(Property p : textures)
			texture = p.getValue();
		return texture;
	}
	
	public static ItemStack setTexture(ItemStack is, String texture) {
		SkullMeta hm = (SkullMeta) is.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			if(texture == null)
				field.set(hm, null);
			else
				field.set(hm, profile);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		is.setItemMeta(hm);
		return is;
	}
}
