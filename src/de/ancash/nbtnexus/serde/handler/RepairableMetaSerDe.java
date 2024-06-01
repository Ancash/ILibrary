package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.REPAIRABLE_REPAIR_COST_TAG;
import static de.ancash.nbtnexus.MetaTag.REPAIRABLE_TAG;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class RepairableMetaSerDe implements IItemSerDe {

	public static final RepairableMetaSerDe INSTANCE = new RepairableMetaSerDe();

	private static final Set<String> bl = Collections.unmodifiableSet(new HashSet<>());

	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(REPAIRABLE_REPAIR_COST_TAG, SerDeStructureEntry.INT);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	RepairableMetaSerDe() {
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		Repairable meta = (Repairable) item.getItemMeta();
		if (meta.hasRepairCost())
			map.put(REPAIRABLE_REPAIR_COST_TAG, meta.getRepairCost());
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof Repairable;
	}

	@Override
	public String getKey() {
		return REPAIRABLE_TAG;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		Repairable meta = (Repairable) item.getItemMeta();
		if (map.containsKey(REPAIRABLE_REPAIR_COST_TAG))
			meta.setRepairCost((int) map.get(REPAIRABLE_REPAIR_COST_TAG));
		item.setItemMeta(meta);
	}
}
