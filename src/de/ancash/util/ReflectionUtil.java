package de.ancash.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ReflectionUtil {

	ReflectionUtil() {
	}

	private static final Map<Class<?>, Map<Class<?>, List<Method>>> cache = new ConcurrentHashMap<Class<?>, Map<Class<?>, List<Method>>>();

	public static List<Method> findMethod(Class<?> clazz, Class<?> returnValue) {
		return cache.computeIfAbsent(clazz, c -> new ConcurrentHashMap<>()).computeIfAbsent(returnValue,
				c -> Arrays.asList(clazz.getDeclaredMethods()).stream()
						.filter(m -> m.getReturnType() != null && returnValue.isAssignableFrom(m.getReturnType()))
						.collect(Collectors.toList()));
	}
}
