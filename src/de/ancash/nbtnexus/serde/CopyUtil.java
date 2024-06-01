package de.ancash.nbtnexus.serde;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public final class CopyUtil {

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> deepCopy(Map<String, Object> map, Function<Map<String, Object>, Map<String, Object>> mapFinalizer,
			Function<ArrayList, ArrayList> listFinalizer) {

		HashMap<String, Object> result = new HashMap<>();

		for (Entry<String, Object> entry : map.entrySet())
			result.put(entry.getKey(), deepCopy(entry.getValue(), mapFinalizer, listFinalizer));

		return mapFinalizer.apply(result);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList deepCopy(List list, Function<Map<String, Object>, Map<String, Object>> mapFinalizer,
			Function<ArrayList, ArrayList> listFinalizer) {

		ArrayList copy = new ArrayList<>(list.size());

		for (int i = 0; i < list.size(); i++)
			copy.add(deepCopy(list.get(i), mapFinalizer, listFinalizer));

		return copy;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object deepCopy(Object val, Function<Map<String, Object>, Map<String, Object>> mapFinalizer,
			Function<ArrayList, ArrayList> listFinalizer) {

		if (val == null || val.getClass().isPrimitive() || val instanceof String || val instanceof Number)
			return val;

		if (val instanceof Map)
			return deepCopy((Map<String, Object>) val, mapFinalizer, listFinalizer);

		if (val instanceof List)
			return deepCopy((List) val, mapFinalizer, listFinalizer);

		if (val.getClass().isArray())
			return deepCopyArray(val, mapFinalizer, listFinalizer);
		return val;
	}

	@SuppressWarnings("rawtypes")
	public static Object deepCopyArray(Object array, Function<Map<String, Object>, Map<String, Object>> mapFinalizer,
			Function<ArrayList, ArrayList> listFinalizer) {

		int length = Array.getLength(array);
		Object test = Array.newInstance(array.getClass().getComponentType(), length);
		for (int i = 0; i < length; i++)
			Array.set(test, i, deepCopy(Array.get(array, i), mapFinalizer, listFinalizer));
		return test;
	}

}
