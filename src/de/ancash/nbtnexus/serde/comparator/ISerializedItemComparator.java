package de.ancash.nbtnexus.serde.comparator;

import java.util.Set;

import de.ancash.nbtnexus.serde.SerializedItem;

public interface ISerializedItemComparator {

	/**
	 * The {@link SerializedItem}s must be identical.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean areEqual(SerializedItem a, SerializedItem b);

	/**
	 * All keys and values of the {@link SerializedItem}s must be equal, except for
	 * the keys specified.
	 * <p>
	 * An ignored key is viewed relative to every {@link SerializedItem} found in
	 * the arguments.
	 * 
	 * @param a
	 * @param b
	 * @param ignoredKeys
	 * @return
	 */
	public boolean areEqualIgnore(SerializedItem a, SerializedItem b, Set<String> ignoredKeys);

	/**
	 * All keys and values of the {@link SerializedItem}s must be equal, except for
	 * the specified keys that are lists or arrays. Here the order of the elements
	 * does not matter.
	 * <p>
	 * The keys are viewed relative to every {@link SerializedItem} found in the
	 * arguments.
	 * 
	 * @param a
	 * @param b
	 * @param ignoredOrder
	 * @return
	 */
	public boolean areEqualIgnoreOrder(SerializedItem a, SerializedItem b, Set<String> ignoredOrder);

	/**
	 * {@link ISerializedItemComparator#areEqualIgnore} and
	 * {@link ISerializedItemComparator#areEqualIgnoreOrder} combined.
	 * 
	 * @param a
	 * @param b
	 * @param ignoredKeys
	 * @param ignoredOrder
	 * @return
	 */
	public boolean areEqual(SerializedItem a, SerializedItem b, Set<String> ignoredKeys, Set<String> ignoredOrder);
}
