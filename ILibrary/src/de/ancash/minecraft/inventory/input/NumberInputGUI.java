package de.ancash.minecraft.inventory.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Tuple;

public class NumberInputGUI<T extends Number> extends StringInputGUI {

	private static final Map<Class<? extends Number>, Method> valueOf = new HashMap<>();

	static {
		valueOf.put(Byte.class, null);
		valueOf.put(Short.class, null);
		valueOf.put(Integer.class, null);
		valueOf.put(Long.class, null);
		valueOf.put(Float.class, null);
		valueOf.put(Double.class, null);
		for (Entry<Class<? extends Number>, Method> entry : valueOf.entrySet()) {
			try {
				entry.setValue(entry.getKey().getDeclaredMethod("valueOf", String.class)); //$NON-NLS-1$
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private final Class<T> clazz;
	private T t;

	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete) {
		this(plugin, player, clazz, onComplete, (t) -> true);
	}

	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete,
			Function<T, Boolean> isValid) {
		super(plugin, player);
		this.clazz = clazz;
		super.isValid(str -> {
			try {
				get(str);
			} catch (Exception ex) {
				t = null;
			}
			return Tuple.of(t != null && (isValid == null ? true : isValid.apply(t)), "Invalid Input: " + str); //$NON-NLS-1$
		});
		super.onComplete(str -> onComplete.accept(t));
	}

	@SuppressWarnings("unchecked")
	private T get(String str) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return t = (T) valueOf.get(clazz).invoke(null, str);
	}
}