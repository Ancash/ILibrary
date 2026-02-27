package de.ancash.minecraft.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.inventory.meta.SkullMeta;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import de.ancash.datastructures.tuples.TriFunction;
import de.ancash.minecraft.AuthLibUtil;

public final class GameProfileUtil {

	GameProfileUtil() {
	}

	private static Field profileField;
	private static Field gameProfileIdField;
	private static Field gameProfileNameField;
	private static Field gameProfilePropertiesField;
	private static Method resolveGameProfile;
	private static Constructor<?> resolvableProfileConstructor;
	private static TriFunction<UUID, String, PropertyMap, GameProfile> gameProfileConstructor;

	static {
		try {
			gameProfileIdField = GameProfile.class.getDeclaredField("id");
			gameProfileIdField.setAccessible(true);
			profileField = ((SkullMeta) XMaterial.PLAYER_HEAD.parseItem().getItemMeta()).getClass()
					.getDeclaredField("profile");
			profileField.setAccessible(true);
			gameProfileNameField = GameProfile.class.getDeclaredField("name");
			gameProfileNameField.setAccessible(true);
			gameProfilePropertiesField = GameProfile.class.getDeclaredField("properties");
			gameProfilePropertiesField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		try {
			Constructor<GameProfile> c1 = GameProfile.class.getConstructor(UUID.class, String.class, PropertyMap.class);
			gameProfileConstructor = (id, name, pm) -> {
				try {
					return c1.newInstance(id, name, pm);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new IllegalStateException(e);
				}
			};
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				Constructor<GameProfile> c2 = GameProfile.class.getConstructor(UUID.class, String.class);
				gameProfileConstructor = (id, name, pm) -> {
					try {
						GameProfile gp = c2.newInstance(id, name, pm);
						gp.getProperties().putAll(pm);
						return gp;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e1) {
						throw new IllegalStateException(e1);
					}
				};
			} catch (NoSuchMethodException | SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	public static GameProfile createGameProfile(UUID id, String name, PropertyMap pm) {
		return gameProfileConstructor.apply(id, name,
				pm == null ? AuthLibUtil.createPropertyMap(ImmutableMultimap.of()) : pm);
	}

	public static String getGameProfileName(GameProfile gp) {
		try {
			return (String) gameProfileNameField.get(gp);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static PropertyMap getGameProfileProperties(GameProfile gp) {
		try {
			return (PropertyMap) gameProfilePropertiesField.get(gp);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static UUID getGameProfileId(GameProfile gp) {
		try {
			return (UUID) gameProfileIdField.get(gp);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
