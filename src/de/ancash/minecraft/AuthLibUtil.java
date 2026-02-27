package de.ancash.minecraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import de.ancash.misc.ConversionUtil;

@SuppressWarnings("nls")
public class AuthLibUtil {

	private static Method getPropertyName;
	private static Method getPropertyValue;
	private static Method getPropertySignature;
	private static final UUID dummyUUID = new UUID(ConversionUtil.bytesToLong(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			ConversionUtil.bytesToLong(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }));
	private static final String dummyName = "dummy";
	private static Field gameProfileUUIDField;
	private static Field gameProfileNameField;
	private static boolean requireAllGameProfileArgumentsNotNull;
	private static Function<Multimap<String, Property>, PropertyMap> propertyMapConstructor;
	private static PropertyMap emptyPropertyMap;

	static {

		try {
			Constructor<PropertyMap> c1 = PropertyMap.class.getConstructor(Multimap.class);
			propertyMapConstructor = map -> {
				try {
					return c1.newInstance(map);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new IllegalStateException(e);
				}
			};
			System.out.println("using immutable multi map constructor for property map");
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				Constructor<PropertyMap> c2 = PropertyMap.class.getConstructor();
				propertyMapConstructor = map -> {
					try {
						PropertyMap pm = c2.newInstance();
						pm.putAll(map);
						return pm;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e1) {
						throw new IllegalStateException(e1);
					}
				};
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new IllegalStateException(e1);
			}

		}

		for (Method m : Property.class.getDeclaredMethods()) {
			if (m.getName().equals("name") || m.getName().equals("getName"))
				getPropertyName = m;
			else if (m.getName().equals("value") || m.getName().equals("getValue"))
				getPropertyValue = m;
			else if (m.getName().equals("signature") || m.getName().equals("getSignature"))
				getPropertySignature = m;
		}
		try {
			gameProfileNameField = GameProfile.class.getDeclaredField("name");
			gameProfileUUIDField = GameProfile.class.getDeclaredField("id");
			gameProfileNameField.setAccessible(true);
			gameProfileUUIDField.setAccessible(true);
		} catch (Throwable th) {
			throw new IllegalStateException(th);
		}

		try {
			new GameProfile(null, dummyName);
			requireAllGameProfileArgumentsNotNull = false;
		} catch (Throwable th) {
			requireAllGameProfileArgumentsNotNull = true;
		}
		
		emptyPropertyMap = propertyMapConstructor.apply(ImmutableMultimap.of());
	}

	public static GameProfile createGameProfile(UUID id, String name, PropertyMap pm) {
		GameProfile dummy = new GameProfile(dummyUUID, dummyName);
		try {
			gameProfileUUIDField.set(dummy, id);
			gameProfileNameField.set(dummy,
					Optional.ofNullable(name).orElse(requireAllGameProfileArgumentsNotNull ? "" : null));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return new GameProfile(id, name);
		}
		dummy.getProperties().putAll(pm);
		return dummy;
	}
	
	public static PropertyMap createPropertyMap(Multimap<String, Property> map) {
		return propertyMapConstructor.apply(map);
	}

	public static String getPropertyName(Property p) {
		try {
			return (String) getPropertyName.invoke(p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String getPropertyValue(Property p) {
		try {
			return (String) getPropertyValue.invoke(p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String getPropertySignature(Property p) {
		try {
			return (String) getPropertySignature.invoke(p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}
}