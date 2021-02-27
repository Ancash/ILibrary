package de.ancash.ilibrary.datastructures.tuples;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
