package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_LEVEL_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_STORAGE_META_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_STORAGE_STORED_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_TYPE_TAG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.cryptomorin.xseries.XEnchantment;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.ancash.nbtnexus.serde.structure.SerDeStructureKeySuggestion;
import de.ancash.nbtnexus.serde.structure.SerDeStructureValueSuggestion;

public class EnchantmentStorageMetaSerDe implements IItemSerDe {

	public static final EnchantmentStorageMetaSerDe INSTANCE = new EnchantmentStorageMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(ENCHANTMENT_STORAGE_STORED_TAG, NBTTag.COMPOUND);
		SerDeStructure ench = structure.getList(ENCHANTMENT_STORAGE_STORED_TAG);
		ench.putEntry(ENCHANTMENT_LEVEL_TAG, SerDeStructureEntry.INT);
		ench.putEntry(ENCHANTMENT_TYPE_TAG,
				new SerDeStructureEntry(
						SerDeStructureKeySuggestion.forStringCollection(XEnchantment.REGISTRY.getValues().stream()
								.map(XEnchantment::name).collect(Collectors.toList())),
						SerDeStructureValueSuggestion.forCustomString(XEnchantment.REGISTRY.getValues(),
								XEnchantment::name, XEnchantment::name)));
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	EnchantmentStorageMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		map.put(ENCHANTMENT_STORAGE_STORED_TAG,
				ItemSerializer.INSTANCE.serializeEnchantments(meta.getStoredEnchants()));
		meta.getStoredEnchants().keySet().forEach(meta::removeStoredEnchant);
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof EnchantmentStorageMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
		ItemDeserializer.INSTANCE
				.deserializeEnchantments((List<Map<String, Object>>) map.get(ENCHANTMENT_STORAGE_STORED_TAG))
				.forEach((e, lvl) -> esm.addStoredEnchant(e, lvl, true));
		item.setItemMeta(esm);
	}

	@Override
	public String getKey() {
		return ENCHANTMENT_STORAGE_META_TAG;
	}
}
