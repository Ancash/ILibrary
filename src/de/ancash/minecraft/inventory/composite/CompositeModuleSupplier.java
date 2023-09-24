package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.Map;

public interface CompositeModuleSupplier {

	public CompositeModule parseModule(String id, List<Integer> slots, Map<String, Object> props);

}
