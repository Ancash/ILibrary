package de.ancash.minecraft;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mojang.authlib.properties.Property;

public class ReflectionUtil {

	private static Method getPropertyName;
	private static Method getPropertyValue;
	private static Method getPropertySignature;

	static {
		for (Method m : Property.class.getDeclaredMethods()) {
			if (m.getName().toLowerCase().contains("name"))
				getPropertyName = m;
			else if (m.getName().toLowerCase().contains("value"))
				getPropertyValue = m;
			else if (m.getName().toLowerCase().contains("signature"))
				getPropertySignature = m;
		}
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