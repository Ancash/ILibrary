package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.ARMOR_TAG;
import static de.ancash.nbtnexus.MetaTag.TRIM_MATERIAL_TAG;
import static de.ancash.nbtnexus.MetaTag.TRIM_PATTERN_TAG;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class ArmorMetaSerDe implements IItemSerDe {

	public static final ArmorMetaSerDe INSTANCE = new ArmorMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		if (XMaterial.COAST_ARMOR_TRIM_SMITHING_TEMPLATE.isSupported()) {

			structure.putEntry(TRIM_MATERIAL_TAG, SerDeStructureEntry.forRegistry(Registry.TRIM_MATERIAL));
			structure.putEntry(TRIM_PATTERN_TAG, SerDeStructureEntry.forRegistry(Registry.TRIM_PATTERN));
		}
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		ArmorMeta meta = (ArmorMeta) item.getItemMeta();
		Map<String, Object> serialized = new HashMap<String, Object>();
		if (meta.hasTrim()) {
			serialized.put(TRIM_MATERIAL_TAG, ItemSerializer.INSTANCE.serializeNamespacedKey(meta.getTrim().getMaterial().getKey()));
			serialized.put(TRIM_PATTERN_TAG, ItemSerializer.INSTANCE.serializeNamespacedKey(meta.getTrim().getPattern().getKey()));
		}
		meta.setTrim(null);
		item.setItemMeta(meta);
		return serialized;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return XMaterial.COAST_ARMOR_TRIM_SMITHING_TEMPLATE.isSupported() && item.getItemMeta() instanceof ArmorMeta;
	}

	@Override
	public String getKey() {
		return ARMOR_TAG;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(TRIM_MATERIAL_TAG)) {
			ArmorMeta meta = (ArmorMeta) item.getItemMeta();
			meta.setTrim(
					new ArmorTrim(Registry.TRIM_MATERIAL.get(ItemDeserializer.INSTANCE.deserializeNamespacedKey((String) map.get(TRIM_MATERIAL_TAG))),
							Registry.TRIM_PATTERN.get(ItemDeserializer.INSTANCE.deserializeNamespacedKey((String) map.get(TRIM_PATTERN_TAG)))));
			item.setItemMeta(meta);
		}
	}

}
