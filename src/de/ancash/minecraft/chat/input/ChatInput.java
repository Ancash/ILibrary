package de.ancash.minecraft.chat.input;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.input.IInput;
import de.ancash.minecraft.input.INumberInput;
import de.ancash.minecraft.input.IStringInput;

public class ChatInput implements IInput {

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player) {
		return newStringInput(pl, player, null);
	}

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete) {
		return newStringInput(pl, player, onComplete, (c) -> Tuple.of(true, null));
	}

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete,
			Function<String, Duplet<Boolean, String>> isValid) {
		return new StringChatInput(pl, player, onComplete, isValid);
	}

	@Override
	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin pl, Player player, Class<T> clazz,
			Consumer<T> onComplete) {
		return newNumberInput(pl, player, clazz, onComplete, (t) -> Tuple.of(t != null, "Invalid number: " + t)); //$NON-NLS-1$
	}

	@Override
	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin plugin, Player player, Class<T> clazz,
			Consumer<T> onComplete, Function<T, Duplet<Boolean, String>> isValid) {
		return new NumberChatInput<>(plugin, player, clazz, onComplete, isValid);
	}
}
