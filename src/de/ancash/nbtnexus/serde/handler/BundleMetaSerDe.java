package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BUNDLE_ITEMS_TAG;
import static de.ancash.nbtnexus.MetaTag.BUNDLE_TAG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class BundleMetaSerDe implements IItemSerDe {

	public static final BundleMetaSerDe INSTANCE = new BundleMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(BUNDLE_ITEMS_TAG, NBTTag.COMPOUND);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	BundleMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		BundleMeta meta = (BundleMeta) item.getItemMeta();
		if (meta.hasItems())
			map.put(BUNDLE_ITEMS_TAG, meta.getItems().stream().filter(i -> i != null).map(i -> ItemSerializer.INSTANCE.serializeItemStack(i))
					.collect(Collectors.toList()));
		meta.setItems(null);
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return XMaterial.BUNDLE.isSupported() && item.getItemMeta() instanceof BundleMeta;
	}

	@Override
	public String getKey() {
		return BUNDLE_TAG;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(BUNDLE_ITEMS_TAG)) {
			BundleMeta meta = (BundleMeta) item.getItemMeta();
			meta.setItems(((List<Map<String, Object>>) map.get(BUNDLE_ITEMS_TAG)).stream().map(ItemDeserializer.INSTANCE::deserializeItemStack)
					.collect(Collectors.toList()));
			item.setItemMeta(meta);
		}
	}
}
