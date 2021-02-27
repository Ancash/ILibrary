package de.ancash.ilibrary.datastructures.tuples;

@FunctionalInterface
public interface QuatroFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
}
