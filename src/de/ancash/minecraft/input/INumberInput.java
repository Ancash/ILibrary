package de.ancash.minecraft.input;

import java.util.function.Consumer;
import java.util.function.Function;

import de.ancash.datastructures.tuples.Duplet;

public interface INumberInput<T extends Number> {

	public INumberInput<T> onComplete(Consumer<T> c);

	public INumberInput<T> isValid(Function<T, Duplet<Boolean, String>> f);

	public void start();

}
