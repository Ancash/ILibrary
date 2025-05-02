package de.ancash.minecraft.crafting;

import java.lang.reflect.Field;

public class ReflectionUtil {

	public static Field findField(Class<?> where, Class<?> val) {
		for (Field f : where.getDeclaredFields()) {
			if (f.getType().equals(val)) {
				f.setAccessible(true);
				return f;
			}
		}
		for (Field f : where.getDeclaredFields()) {
			if (val.isAssignableFrom(f.getType())) {
				f.setAccessible(true);
				return f;
			}
		}
		return null;
	}

}
