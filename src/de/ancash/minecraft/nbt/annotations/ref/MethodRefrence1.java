package de.ancash.minecraft.nbt.annotations.ref;

import java.io.Serializable;

public interface MethodRefrence1<T> extends Serializable {
	void callable(T obj);
}