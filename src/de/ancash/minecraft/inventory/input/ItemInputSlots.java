package de.ancash.minecraft.inventory.input;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemInputSlots {

	private final Set<Integer> inputSlots;
	
	public ItemInputSlots() {
		this(new HashSet<>());
	}
	
	public ItemInputSlots(Collection<Integer> slots) {
		inputSlots = new HashSet<>(slots);
	}
	
	public void addInputSlots(int...slots) {
		for(int i : slots)
			inputSlots.add(i);
	}
	
	public void addInputSlots(Collection<Integer> slots) {
		inputSlots.addAll(slots);
	}
	
	public Set<Integer> getInputSlots() {
		return inputSlots;
	}
}