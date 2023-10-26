package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.Map;

public interface CompositeModuleParser {

	public CompositeModuleSupplier parseModule(String id, List<Integer> slots, Map<String, Object> props);

}
