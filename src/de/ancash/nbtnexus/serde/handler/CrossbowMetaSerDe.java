package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.CROSSBOW_CHARGED_PROJECTILES_TAG;
import static de.ancash.nbtnexus.MetaTag.CROSSBOW_META_TAG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class CrossbowMetaSerDe implements IItemSerDe {

	public static final CrossbowMetaSerDe INSTANCE = new CrossbowMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(CROSSBOW_CHARGED_PROJECTILES_TAG, NBTTag.COMPOUND);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	CrossbowMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
		if (meta.hasChargedProjectiles()) {
			map.put(CROSSBOW_CHARGED_PROJECTILES_TAG,
					meta.getChargedProjectiles().stream().map(ItemSerializer.INSTANCE::serializeItemStack).collect(Collectors.toList()));
			meta.setChargedProjectiles(null);
			item.setItemMeta(meta);
		}
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof CrossbowMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		CrossbowMeta bm = (CrossbowMeta) item.getItemMeta();
		List<Map<String, Object>> projectiles = (List<Map<String, Object>>) map.get(CROSSBOW_CHARGED_PROJECTILES_TAG);
		if (projectiles != null)
			bm.setChargedProjectiles(projectiles.stream().map(ItemDeserializer.INSTANCE::deserializeItemStack).collect(Collectors.toList()));
		item.setItemMeta(bm);
	}

	@Override
	public String getKey() {
		return CROSSBOW_META_TAG;
	}
}
