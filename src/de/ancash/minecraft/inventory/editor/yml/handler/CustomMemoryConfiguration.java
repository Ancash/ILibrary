package de.ancash.minecraft.inventory.editor.yml.handler;

import de.ancash.libs.org.simpleyaml.configuration.ConfigurationSection;
import de.ancash.libs.org.simpleyaml.configuration.MemoryConfiguration;

public class CustomMemoryConfiguration extends MemoryConfiguration {

	protected final ConfigurationSection parent;

	public CustomMemoryConfiguration(ConfigurationSection parent) {
		this.parent = parent;
	}

	@Override
	public ConfigurationSection getParent() {
		return parent;
	}
}
