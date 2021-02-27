package de.ancash.ilibrary.minecraft.nbt.annotations.ref;

import java.io.Serializable;

public interface MethodRefrence2<T, Z> extends Serializable {
	void callable(T obj, Z obj2);
}