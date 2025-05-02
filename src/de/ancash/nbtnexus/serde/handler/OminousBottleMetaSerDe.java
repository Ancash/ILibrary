package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BOOK_TAG;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.OminousBottleMeta;

import de.ancash.nbtnexus.MetaTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.tr7zw.nbtapi.utils.MinecraftVersion;

public class OminousBottleMetaSerDe implements IItemSerDe {

	public static final OminousBottleMetaSerDe INSTANCE = new OminousBottleMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(MetaTag.OMINOUS_BOTTLE_AMPLIFIER_TAG, SerDeStructureEntry.INT);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	OminousBottleMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		OminousBottleMeta meta = (OminousBottleMeta) item.getItemMeta();
		if (meta.hasAmplifier()) {
			map.put(MetaTag.OMINOUS_BOTTLE_AMPLIFIER_TAG, meta.getAmplifier());
			meta.setAmplifier(0);
			item.setItemMeta(meta);
		}
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_21_R1) && item.getItemMeta() instanceof OminousBottleMeta;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		OminousBottleMeta bm = (OminousBottleMeta) item.getItemMeta();
		if (map.containsKey(MetaTag.OMINOUS_BOTTLE_AMPLIFIER_TAG))
			bm.setAmplifier((int) map.get(MetaTag.OMINOUS_BOTTLE_AMPLIFIER_TAG));
		item.setItemMeta(bm);
	}

	@Override
	public String getKey() {
		return BOOK_TAG;
	}
}
