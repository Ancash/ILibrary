package de.ancash.nbtnexus.packet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTNexusItem;
import de.ancash.nbtnexus.serde.SerializedItem;

public class ItemComputerRegistry {

	ItemComputerRegistry() {

	}

	private static final ConcurrentHashMap<String, IItemComputer> defaultComputer = new ConcurrentHashMap<String, IItemComputer>();
	private static final ConcurrentHashMap<String, IItemComputer> placeholderComputer = new ConcurrentHashMap<String, IItemComputer>();
	private static Logger logger = NBTNexus.getInstance().pl.getLogger();

	@SuppressWarnings("nls")
	public static void registerComputer(IItemComputer ipic, ListenerPolicy policy, JavaPlugin plugin) {
		Validate.notNull(ipic, "IItemComputer null");
		Validate.notNull(policy, "ListenerPolicy null");
		Validate.notNull(plugin, "Plugin null");
		if (!NBTNexus.getInstance().enableExperimentalPacketEditing()) {
			logger.warning(plugin.getName() + " tried to register an IItemComputer although experimental packet editing is disabled!");
			return;
		}
		logger.warning(plugin.getName() + " registered an IItemComputer which is an experimental feature!");
		switch (policy) {
		case DEFAULT:
			defaultComputer.put(plugin.getName(), ipic);
			break;
		case PLACEHOLDER:
			placeholderComputer.put(plugin.getName(), ipic);
			break;
		default:
			break;
		}
		logger.info(plugin.getName() + " registered an IItemComputer with " + policy + " policy");
	}

	@SuppressWarnings("nls")
	public static boolean unregisterComputer(JavaPlugin pl, ListenerPolicy p) {
		switch (p) {
		case DEFAULT:
			if (defaultComputer.remove(pl.getName()) != null) {
				logger.info(pl.getName() + " unregistered an IItemComputer with " + p + " policy");
				return true;
			}
			return false;
		case PLACEHOLDER:
			if (placeholderComputer.remove(pl.getName()) != null) {
				logger.info(pl.getName() + " unregistered an IItemComputer with " + p + " policy");
				return true;
			}
			return false;
		default:
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("nls")
	public static ItemStack computeItem(ItemStack item) {
		SerializedItem si = SerializedItem.of(item);
		item = si.toItem();
		Map<String, Object> nexus = si.getMap(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG);
		switch (NBTNexusItem.Type.valueOf((String) nexus.get(NBTNexusItem.NBT_NEXUS_ITEM_TYPE_TAG))) {
		case PLACEHOLDER:
			String owner = (String) nexus.get(NBTNexusItem.NBT_NEXUS_ITEM_OWNER_TAG);
			IItemComputer computer = placeholderComputer.get(owner);
			if (computer == null)
				return item;
			try {
				return computer.computePlaceholder(item);
			} catch (Throwable th) {
				logger.severe(owner + " threw exception on computing item");
				th.printStackTrace();
				logger.severe(si.getMap().toString());
			}
			return item;
		case SERIALIZED:
			for (Entry<String, IItemComputer> entry : defaultComputer.entrySet()) {
				try {
					item = entry.getValue().computeDefault(si.toItem(), item);
					if (item == null)
						return null;
				} catch (Throwable th) {
					logger.severe(entry.getKey() + " threw exception on computing item");
					th.printStackTrace();
					logger.severe(si.getMap().toString());
				}
			}
			return item;
		default:
			throw new IllegalStateException();
		}
	}

	public enum ListenerPolicy {
		DEFAULT, PLACEHOLDER;
	}
}
