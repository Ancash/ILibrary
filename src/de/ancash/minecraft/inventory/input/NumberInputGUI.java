package de.ancash.minecraft.inventory.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.input.INumberInput;

public class NumberInputGUI<T extends Number> implements INumberInput<T> {

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
	private final StringInputGUI sig;
	private Function<T, Duplet<Boolean, String>> isValid;
	private Consumer<T> onComplete;

	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete) {
		this(plugin, player, clazz, onComplete, (t) -> Tuple.of(true, null));
	}

	public NumberInputGUI(JavaPlugin plugin, Player player, Class<T> clazz, Consumer<T> onComplete, Function<T, Duplet<Boolean, String>> isValid) {
		this.clazz = clazz;
		this.isValid = isValid;
		sig = new StringInputGUI(plugin, player);
		sig.isValid(this::isNumber);
		onComplete(onComplete);
	}

	private Duplet<Boolean, String> isNumber(String str) {
		try {
			get(str);
		} catch (Exception ex) {
			t = null;
			return Tuple.of(false, sig.getText());
		}
		return isValid.apply(t);
	}

	public NumberInputGUI<T> setLeft(ItemStack left) {
		sig.setLeft(left);
		return this;
	}

	public NumberInputGUI<T> setRight(ItemStack right) {
		sig.setRight(right);
		return this;
	}

	public NumberInputGUI<T> setTitle(String title) {
		sig.setTitle(title);
		return this;
	}

	public NumberInputGUI<T> setText(String text) {
		sig.setText(text);
		return this;
	}

	@SuppressWarnings("unchecked")
	private T get(String str) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return t = (T) valueOf.get(clazz).invoke(null, str);
	}

	@Override
	public NumberInputGUI<T> onComplete(Consumer<T> c) {
		this.onComplete = c;
		sig.onComplete(str -> this.onComplete.accept(t));
		return this;
	}

	@Override
	public NumberInputGUI<T> isValid(Function<T, Duplet<Boolean, String>> f) {
		this.isValid = f;
		return this;
	}

	@Override
	public void start() {
		sig.start();
	}
}