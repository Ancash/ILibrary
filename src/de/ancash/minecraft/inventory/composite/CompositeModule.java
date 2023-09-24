package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Triplet;

public class CompositeModule {

	protected final List<CompositeIGUIModuleWrapper> modules;

	CompositeModule(CompositeGUI gui, ItemStack item, List<Integer> slots,
			Consumer<Triplet<Integer, Boolean, InventoryAction>> onClick) {
		this.modules = slots.stream().map(s -> new CompositeIGUIModuleWrapper(gui, item, s, onClick))
				.collect(Collectors.toList());
	}

	public void setCanBeEnabled(Supplier<Boolean> enable) {
		modules.forEach(c -> c.setCanBeEnabled(enable));
	}

	public void setOnDisable(Consumer<CompositeGUI> onDisable) {
		modules.forEach(c -> c.setOnDisable(onDisable));
	}

	public void setOnEnable(Consumer<CompositeGUI> onEnable) {
		modules.forEach(c -> c.setOnEnable(onEnable));
	}

	public void setOnUpdate(Consumer<CompositeGUI> onUpdate) {
		modules.forEach(c -> c.setOnUpdate(onUpdate));
	}

	public void setOnDisabledClick(Consumer<CompositeGUI> onDisabledClick) {
		modules.forEach(c -> c.setOnDisabledClick(onDisabledClick));
	}

	public List<Integer> getSlots() {
		return modules.stream().map(CompositeIGUIModuleWrapper::getSlot).collect(Collectors.toList());
	}

	public List<ItemStack> getItems() {
		return modules.stream().map(c -> c.getItem()).collect(Collectors.toList());
	}
}
