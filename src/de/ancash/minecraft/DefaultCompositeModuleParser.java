package de.ancash.minecraft;

import java.util.List;
import java.util.Map;

import de.ancash.minecraft.inventory.composite.CompositeModuleParser;
import de.ancash.minecraft.inventory.composite.CompositeModuleSupplier;

public class DefaultCompositeModuleParser implements CompositeModuleParser {

	@Override
	public CompositeModuleSupplier parseModule(String id, List<Integer> slots, Map<String, Object> props) {
		String lower = id.toLowerCase();
		if (lower.startsWith("close")) {

		}
		return null;
	}

}
