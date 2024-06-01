package de.ancash.nbtnexus.serde;

import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public interface IItemSerDe {

	public Map<String, Object> serialize(ItemStack item);

	public boolean isValid(ItemStack item);

	public SerDeStructure getStructure();

	public String getKey();

	public default Set<String> getBlacklistedKeys() {
		return null;
	}

	public default boolean hasBlacklistedKeys() {
		return getBlacklistedKeys() != null && !getBlacklistedKeys().isEmpty();
	}

//	@Deprecated
//	public default boolean hasKeysToRelocate() {
//		return getKeysToRelocate() != null && !getKeysToRelocate().isEmpty();
//	}
//
//	@Deprecated
//	public default Map<String, String> getKeysToRelocate() {
//		return null;
//	}

	public void deserialize(ItemStack item, Map<String, Object> map);

//	@Deprecated
//	public default boolean hasKeysToReverseRelocate() {
//		return getKeysToReverseRelocate() != null && !getKeysToReverseRelocate().isEmpty();
//	}

//	@Deprecated
//	public default Map<String, String> getKeysToReverseRelocate() {
//		return null;
//	}
}
