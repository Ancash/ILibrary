package de.ancash.minecraft.inventory.composite;

import java.util.List;

public class CompositeGUISupplier {

	private final String title;
	private final int size;
	private final List<CompositeModule> mods;

	public CompositeGUISupplier(String title, int size, List<CompositeModule> mods) {
		this.title = title;
		this.size = size;
		this.mods = mods;
	}

	public String getTitle() {
		return title;
	}

	public int getSize() {
		return size;
	}

	public List<CompositeModule> getModules() {
		return mods;
	}
}
