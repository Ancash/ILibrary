package de.ancash.ilibrary.datastructures.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public class Unit<A> implements Serializable{

	private static final long serialVersionUID = 9075308194067634453L;

	private  A first;

    private Unit(A first) {
        this.first = first;
    }

    public static <A, B> Function<Unit<A>, Duplet<A, B>> mapToDuplet(Function<? super A, ? extends B> mapFirst) {
        return unit -> Duplet.of(unit.first, mapFirst.apply(unit.first));
    }

    public <B> Duplet<A, B> add(B second) {
        return Duplet.of(first, second);
    }

    public <B> Unit<B> map(Function<? super A, ? extends B> mapFirst) {
        return new Unit<>(mapFirst.apply(first));
    }

    public static <A> Unit<A> of(A first) {
        return new Unit<>(first);
    }

    public A getFirst() {
        return first;
    }
    
    public void setFirst(A first) {
    	this.first = first;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Unit{");
        sb.append("first=").append(first);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit<?> unit = (Unit<?>) o;
        return Objects.equals(first, unit.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first);
    }
}
