package de.ancash.minecraft.inventory.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParsePosition;
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
	
	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete) {
		this(plugin, player, clazz, onComplete, (t) -> true);
	}
	
	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete, Function<T, Boolean> isValid) {
		super(plugin, player);
		this.clazz = clazz;
		super.isValid(str -> {
			ParsePosition pos = new ParsePosition(0);
			NumberFormat.getInstance().parse(str, pos);
			return Tuple.of(str.length() == pos.getIndex() && (isValid == null ? true : isValid.apply(get(str))), "Invalid Input: " + str); //$NON-NLS-1$
		});
		super.onComplete(str -> {
			onComplete.accept(get(str));
		});
	}
	
	@SuppressWarnings("unchecked")
	private T get(String str) {
		try {
			return (T) valueOf.get(clazz).invoke(null, str);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}