package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.SPAWN_EGG_TAG;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class SpawnEggMetaSerDe implements IItemSerDe {

	public static final SpawnEggMetaSerDe INSTANCE = new SpawnEggMetaSerDe();

	SpawnEggMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
//		Map<String, Object> map = new HashMap<>();
//		SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
//		XMaterial type = XMaterial.matchXMaterial(item);
//		map.put(SPAWN_EGG_TYPE_TAG, type.name());
//		item.setItemMeta(meta);
		return new HashMap<>();
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof SpawnEggMeta;
	}

	@Override
	public String getKey() {
		return SPAWN_EGG_TAG;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
//		item.setType(XMaterial.matchXMaterial((String) map.get(SPAWN_EGG_TYPE_TAG)).get().parseMaterial());
//		SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
//		item.setItemMeta(meta);
	}

	@Override
	public SerDeStructure getStructure() {
		return null;
	}
}
