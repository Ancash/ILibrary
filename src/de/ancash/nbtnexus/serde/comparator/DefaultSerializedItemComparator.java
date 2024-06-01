package de.ancash.nbtnexus.serde.comparator;

import java.util.HashSet;
import java.util.Set;

import de.ancash.nbtnexus.serde.SerializedItem;

public class DefaultSerializedItemComparator implements ISerializedItemComparator {

	public static final DefaultSerializedItemComparator INSTANCE = new DefaultSerializedItemComparator();

	@SuppressWarnings("nls")
	@Override
	public boolean areEqual(SerializedItem a, SerializedItem b) {
		return SerializedItemComparatorUtil.compareMap(a.getMap(), b.getMap(), new HashSet<>(), new HashSet<>(), "");
	}

	@SuppressWarnings("nls")
	@Override
	public boolean areEqualIgnore(SerializedItem a, SerializedItem b, Set<String> ignoredKeys) {
		return SerializedItemComparatorUtil.compareMap(a.getMap(), b.getMap(), ignoredKeys, new HashSet<>(), "");
	}

	@SuppressWarnings("nls")
	@Override
	public boolean areEqualIgnoreOrder(SerializedItem a, SerializedItem b, Set<String> ignoredOrder) {
		return SerializedItemComparatorUtil.compareMap(a.getMap(), b.getMap(), new HashSet<>(), ignoredOrder, "");
	}

	@SuppressWarnings("nls")
	@Override
	public boolean areEqual(SerializedItem a, SerializedItem b, Set<String> ignoredKeys, Set<String> ignoredOrder) {
		return SerializedItemComparatorUtil.compareMap(a.getMap(), b.getMap(), ignoredKeys, ignoredOrder, "");
	}

}
