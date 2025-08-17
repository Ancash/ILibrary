package de.ancash.minecraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.tr7zw.nbtapi.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("nls")
public class ItemStackUtils {

	private static Field profileField;
	private static Field gameProfileIdField;
	private static Method resolveGameProfile;
	private static Constructor<?> resolvableProfileConstructor;

	static {
		try {
			gameProfileIdField = GameProfile.class.getDeclaredField("id");
			gameProfileIdField.setAccessible(true);
			profileField = ((SkullMeta) XMaterial.PLAYER_HEAD.parseItem().getItemMeta()).getClass()
					.getDeclaredField("profile");
			profileField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		findResolvableProfileToGameProfileMethod();
		findResolvableProfileConstructor();
	}

	private static void findResolvableProfileConstructor() {
		Class<?> clazz;
		try {
			clazz = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
			resolvableProfileConstructor = clazz.getDeclaredConstructor(GameProfile.class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			return;
		}
	}
	
	static void findResolvableProfileToGameProfileMethod() {
		Class<?> clazz;
		try {
			clazz = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
		} catch (ClassNotFoundException e) {
			return;
		}
		for (Method m : clazz.getDeclaredMethods()) {
			if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(GameProfile.class)) {
				resolveGameProfile = m;
				System.out.println("found " + m.getDeclaringClass().getCanonicalName() + "#" + m.getName()
						+ " to convert profiles");
				break;
			}
		}
	}

	public static String translateChatColor(String textToTranslate, char to) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++)
			if (b[i] == ChatColor.COLOR_CHAR && ChatColor.ALL_CODES.indexOf(b[i + 1]) > -1)
				b[i] = to;
		return new String(b);
	}

	public static ItemStack replacePlaceholder(ItemStack is, Map<String, String> placeholder) {
		ItemMeta im = is.getItemMeta();
		if (im.hasLore())
			im.setLore(replacePlaceholder(im.getLore(), placeholder));
		is.setItemMeta(im);
		return is;
	}

	public static List<String> replacePlaceholder(List<String> toReplace, Map<String, String> placeholder) {
		if (toReplace == null)
			return null;
		List<String> lore = new ArrayList<String>();
		for (String str : toReplace) {
			for (String place : placeholder.keySet())
				if (str.contains(place))
					str = str.replace(place, placeholder.get(place) == null ? place : placeholder.get(place));

			if (str.contains("\n"))
				for (String s : str.split("\n"))
					lore.add(s);
			else
				lore.add(str);
		}
		return lore;
	}

	public static String replaceString(String str, Map<String, String> placeholder) {
		for (String s : placeholder.keySet())
			if (str.contains(s))
				str = str.replace(s, placeholder.get(s) == null ? s : placeholder.get(s));
		return str;
	}

	public static String getDisplayName(ItemStack item) {
		return !item.getItemMeta().hasDisplayName() ? XMaterial.matchXMaterial(item).toString()
				: item.getItemMeta().getDisplayName();
	}

	public static ItemStack setDisplayname(ItemStack is, String str) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', str));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack setLore(ItemStack is, String... str) {
		return setLore(Arrays.asList(str), is);
	}

	public static ItemStack setLore(List<String> lore, ItemStack is) {
		ItemMeta im = is.getItemMeta();
		List<String> a = new ArrayList<String>();
		for (String s : lore)
			for (String b : s.split("\n"))
				a.add(b);
		im.setLore(a);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack addItemFlag(ItemStack item, ItemFlag flag) {
		ItemMeta m = item.getItemMeta();
		m.addItemFlags(flag);
		item.setItemMeta(m);
		return item;
	}

	public static ItemStack removeLine(String hasToContain, ItemStack is) {
		ItemMeta im = is.getItemMeta();
		List<String> newLore = new ArrayList<String>();
		for (String str : im.getLore())
			if (!str.contains(hasToContain))
				newLore.add(str);
		im.setLore(newLore);
		is.setItemMeta(im);
		return is;
	}

	@SuppressWarnings({ "deprecation" })
	public static void setItemStack(FileConfiguration fc, String path, ItemStack item) {
		item = item.clone();
		if (item.getItemMeta() instanceof SkullMeta) {

			if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1)) {
				fc.set(path, item);
			} else {
				String txt = null;
				try {
					txt = ItemStackUtils.getTexure(item);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
						| InvocationTargetException e) {

				}
				SkullMeta sm = (SkullMeta) item.getItemMeta();
				if (txt == null) {
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

	public static ItemStack getItemStack(FileConfiguration fc, String path) {
		try {
			return getItemStack0(fc, path);
		} catch (Exception ex) {
			return ItemStackFileUtil.getItemStack(fc, path);
		}
	}

	private static ItemStack getItemStack0(FileConfiguration fc, String path) {
		ItemStack item = fc.getItemStack(path).clone();
		if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_14_R1))
			if (fc.getString(path + "-texture") != null)
				item = setTexture(item, fc.getString(path + "-texture"));
		return item;
	}

	@Deprecated
	public static void set(FileConfiguration fc, String path, ItemStack is) {
		setItemStack(fc, path, is);
	}

	public static ItemStack setProfileName(ItemStack is, String name) {
		SkullMeta hm = (SkullMeta) is.getItemMeta().clone();
		try {
			GameProfile profile = getGameProfile(is);
			Field nameField = profile.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(profile, name);
		} catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		is.setItemMeta(hm);
		return is;
	}

	public static String getTexure(ItemStack is) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String texture = null;
		GameProfile profile = getGameProfile(is);
		Collection<Property> textures = profile.getProperties().get("textures");
		for (Property p : textures)
			texture = AuthLibUtil.getPropertyValue(p);
		return texture;
	}

	public static GameProfile getGameProfile(ItemStack is) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return getGameProfile(is.getItemMeta());
	}

	public static GameProfile getGameProfile(ItemMeta im) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object o = profileField.get(im);
		if (o == null) {
			return null;
		}
		if (o.getClass().getName().equals("net.minecraft.world.item.component.ResolvableProfile")) {
			return (GameProfile) resolveGameProfile.invoke(o);
		}
		return (GameProfile) o;
	}

	public static ItemStack setGameProfileId(ItemStack item, UUID id) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		item.setItemMeta(setGameProfileId(item.getItemMeta(), id));
		return item;
	}

	public static ItemMeta setGameProfileId(ItemMeta im, UUID id) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		GameProfile gameProfile = getGameProfile(im);
		gameProfileIdField.set(gameProfile, id);
		setGameProfile(im, gameProfile);
		return im;
	}
	
	public static void setGameProfile(ItemMeta im, GameProfile profile) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		if(resolvableProfileConstructor != null) {
			profileField.set(im, resolvableProfileConstructor.newInstance(profile));
		} else {
			profileField.set(im, profile);
		}
	}

	public static ItemStack setTexture(ItemStack is, String texture) {
		SkullMeta hm = (SkullMeta) is.getItemMeta();
		GameProfile profile = AuthLibUtil.createGameProfile(
				texture != null ? new UUID(texture.hashCode(), texture.hashCode()) : UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			if (texture == null)
				field.set(hm, null);
			else
				field.set(hm, profile);
		} catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		is.setItemMeta(hm);
		return is;
	}
}
