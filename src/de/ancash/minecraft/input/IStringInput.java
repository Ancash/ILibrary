package de.ancash.minecraft.input;

import java.util.function.Consumer;
import java.util.function.Function;

import de.ancash.datastructures.tuples.Duplet;

public interface IStringInput {

	public IStringInput onComplete(Consumer<String> c);

	public IStringInput isValid(Function<String, Duplet<Boolean, String>> f);

	public void start();
}
