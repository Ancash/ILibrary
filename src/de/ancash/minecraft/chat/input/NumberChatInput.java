package de.ancash.minecraft.chat.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.input.INumberInput;

public class NumberChatInput<T extends Number> implements INumberInput<T> {

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

	private final Class<? extends Number> clazz;

	private Consumer<T> onComplete;
	private final StringChatInput sci;
	private Function<T, Duplet<Boolean, String>> isNumValid;
	private T t;

	public NumberChatInput(JavaPlugin pl, Player player, Class<T> clazz, Consumer<T> onComplete) {
		this(pl, player, clazz, onComplete, (c) -> Tuple.of(true, null));
	}

	public NumberChatInput(JavaPlugin pl, Player player, Class<T> clazz, Consumer<T> onComplete, Function<T, Duplet<Boolean, String>> isValid) {
		this.sci = new StringChatInput(pl, player);
		this.isNumValid = isValid;
		sci.isValid(this::onInput);
		onComplete(onComplete);
		this.clazz = clazz;
	}

	private Duplet<Boolean, String> onInput(String in) {
		try {
			t = get(in);
		} catch (Exception e) {
			t = null;
		}
		return isNumValid.apply(t);
	}

	@SuppressWarnings("unchecked")
	private T get(String str) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) valueOf.get(clazz).invoke(null, str);
	}

	@Override
	public NumberChatInput<T> onComplete(Consumer<T> c) {
		this.onComplete = c;
		sci.onComplete(r -> this.onComplete.accept(t));
		return this;
	}

	@Override
	public NumberChatInput<T> isValid(Function<T, Duplet<Boolean, String>> f) {
		this.isNumValid = f;
		return this;
	}

	public NumberChatInput<T> setInputMessage(String s) {
		sci.setInitialInputMessage(s);
		return this;
	}

	@Override
	public void start() {
		sci.start();
	}
}
