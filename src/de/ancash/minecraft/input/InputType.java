package de.ancash.minecraft.input;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import de.ancash.minecraft.chat.input.ChatInput;
import de.ancash.minecraft.inventory.input.AnvilInput;

public enum InputType {

	ANVIL(AnvilInput.class), CHAT(ChatInput.class);

	private final Class<? extends IInput> clazz;

	private InputType(Class<? extends IInput> clazz) {
		this.clazz = clazz;
	}

	public IInput newInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public IInput newInstance(Object... objs) {
		try {
			return clazz.getConstructor(Arrays.asList(objs).stream().map(Object::getClass).toArray(Class[]::new)).newInstance(objs);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new IllegalStateException(e);
		}
	}
}
