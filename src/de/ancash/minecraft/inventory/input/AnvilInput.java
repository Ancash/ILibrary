package de.ancash.minecraft.inventory.input;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.input.IInput;
import de.ancash.minecraft.input.INumberInput;
import de.ancash.minecraft.input.IStringInput;

public class AnvilInput implements IInput {

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player) {
		return newStringInput(pl, player, null);
	}

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete) {
		return newStringInput(pl, player, null, (c) -> Tuple.of(true, null));
	}

	@Override
	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete,
			Function<String, Duplet<Boolean, String>> isValid) {
		return new StringInputGUI(pl, player, onComplete, isValid);
	}

	@Override
	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin pl, Player player, Class<T> clazz,
			Consumer<T> onComplete) {
		return newNumberInput(pl, player, clazz, onComplete, (t) -> Tuple.of(t != null, "Invalid number: " + t)); //$NON-NLS-1$
	}

	@Override
	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin pl, Player player, Class<T> clazz,
			Consumer<T> onComplete, Function<T, Duplet<Boolean, String>> isValid) {
		return new NumberInputGUI<T>(pl, player, clazz, onComplete, isValid);
	}
}
