package de.ancash.minecraft.inventory.composite;

import java.util.List;
import java.util.UUID;

public class CompositeGUISupplier {

	private final String title;
	private final int size;
	private final List<CompositeModuleSupplier> mods;

	public CompositeGUISupplier(String title, int size, List<CompositeModuleSupplier> mods) {
		this.title = title;
		this.size = size;
		this.mods = mods;
	}

	public CompositeGUI newInstance(UUID id) {
		CompositeGUI gui = new CompositeGUI(id, size, title);
		mods.stream().map(cms -> cms.newInstance(gui)).forEach(cm -> gui.addModule(cm));
		return gui;
	}
}
