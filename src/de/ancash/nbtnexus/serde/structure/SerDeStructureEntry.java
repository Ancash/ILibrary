package de.ancash.nbtnexus.serde.structure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Keyed;
import org.bukkit.Registry;

import de.ancash.ILibrary;
import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.nbtnexus.serde.ItemSerializer;

public class SerDeStructureEntry {

	private static Map<Class<?>, Method> enumValuesMethod = new ConcurrentHashMap<Class<?>, Method>();
	private static Method enumNameMethod;
	
	static {
		try {
			enumNameMethod = Class.forName("org.bukkit.util.OldEnum").getDeclaredMethod("name");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			System.err.println("could not find old enum class: " + e);
		}
	}
	
	private static Object[] getOldValues(Class<?> clazz) {
		try {
			if (!enumValuesMethod.containsKey(clazz)) {
				enumValuesMethod.put(clazz, clazz.getDeclaredMethod("values"));
			}
			return (Object[]) enumValuesMethod.get(clazz).invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new IllegalStateException("could not get values of " + clazz, e);
		}
	}

	private static String getName(Class<?> clazz, Object o) {
		try {
			return (String) enumNameMethod.invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new IllegalStateException("could not get name of " + clazz, e);
		}
	}

	public static final SerDeStructureEntry STRING = new SerDeStructureEntry(SerDeStructureKeySuggestion.STRING, null);
	public static final SerDeStructureEntry BYTE = new SerDeStructureEntry(SerDeStructureKeySuggestion.BYTE, null);
	public static final SerDeStructureEntry SHORT = new SerDeStructureEntry(SerDeStructureKeySuggestion.SHORT, null);
	public static final SerDeStructureEntry INT = new SerDeStructureEntry(SerDeStructureKeySuggestion.INT, null);
	public static final SerDeStructureEntry LONG = new SerDeStructureEntry(SerDeStructureKeySuggestion.LONG, null);
	public static final SerDeStructureEntry FLOAT = new SerDeStructureEntry(SerDeStructureKeySuggestion.FLOAT, null);
	public static final SerDeStructureEntry DOUBLE = new SerDeStructureEntry(SerDeStructureKeySuggestion.DOUBLE, null);
	public static final SerDeStructureEntry BOOLEAN = new SerDeStructureEntry(SerDeStructureKeySuggestion.BOOLEAN,
			null);
	public static final SerDeStructureEntry UUID = new SerDeStructureEntry(SerDeStructureKeySuggestion.UUID,
			SerDeStructureValueSuggestion.forUUID());

	protected final SerDeStructureKeySuggestion<?> key;
	protected final SerDeStructureValueSuggestion<?> value;

	public SerDeStructureEntry(SerDeStructureKeySuggestion<?> key, SerDeStructureValueSuggestion<?> value) {
		this.key = key;
		this.value = value;
	}

	public SerDeStructureKeySuggestion<?> getKey() {
		return key;
	}

	public SerDeStructureValueSuggestion<?> getValue() {
		return value;
	}

	public static <T extends Keyed> SerDeStructureEntry forRegistry(Registry<T> registry) {
		Iterator<T> iter = registry.iterator();
		List<ValueSuggestion<String>> suggestions = new ArrayList<ValueSuggestion<String>>();
		while (iter.hasNext()) {
			String ser = ItemSerializer.INSTANCE.serializeNamespacedKey(iter.next().getKey());
			suggestions.add(new ValueSuggestion<String>(StringHandler.INSTANCE, ser, ser));
		}
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forRegistry(registry),
				new SerDeStructureValueSuggestion<String>(suggestions));
	}

	public static <T extends Enum<T>> SerDeStructureEntry forEnum(Class<T> clazz) {
		if(!clazz.isEnum())
			return forOldEnum(clazz);
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forEnum(clazz),
				SerDeStructureValueSuggestion.forEnum(clazz));
	}

	public static SerDeStructureEntry forOldEnum(Class<?> clazz) {
		ILibrary.getInstance().getLogger().info(clazz.getSimpleName() + " treated as old enum");
		List<String> names = Arrays.asList(getOldValues(clazz)).stream().map(o -> getName(clazz, o))
				.collect(Collectors.toList());
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forStringCollection(names),
				new SerDeStructureValueSuggestion<String>(names.stream()
						.map(e -> new ValueSuggestion<>(StringHandler.INSTANCE, e, e)).collect(Collectors.toList())));
	}

	public static <T extends Enum<T>> SerDeStructureEntry forStringCollection(Collection<String> col) {
		return new SerDeStructureEntry(SerDeStructureKeySuggestion.forStringCollection(col),
				SerDeStructureValueSuggestion.forStringCollection(col));
	}
}
