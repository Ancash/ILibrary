package de.ancash.minecraft.input;

import java.util.function.Consumer;
import java.util.function.Function;

import de.ancash.datastructures.tuples.Duplet;

public interface IStringInput {

	public void onComplete(Consumer<String> c);

	public void isValid(Function<String, Duplet<Boolean, String>> f);

	public void start();
}
