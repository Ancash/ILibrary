package de.ancash.minecraft.input;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.datastructures.tuples.Duplet;

public interface IInput {

	public IStringInput newStringInput(JavaPlugin pl, Player player);

	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete);

	public IStringInput newStringInput(JavaPlugin pl, Player player, Consumer<String> onComplete,
			Function<String, Duplet<Boolean, String>> isValid);

	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin pl, Player player, Class<T> clazz,
			Consumer<T> onComplete);

	public <T extends Number> INumberInput<T> newNumberInput(JavaPlugin plugin, Player player, Class<T> clazz,
			Consumer<T> onComplete, Function<T, Duplet<Boolean, String>> isValid);
}
