package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.AXOLOTL_BUCKET_TAG;
import static de.ancash.nbtnexus.MetaTag.AXOLOTL_BUCKET_VARIANT_TAG;
import static de.ancash.nbtnexus.NBTNexus.SPLITTER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

@SuppressWarnings("nls")
public class AxolotlBucketMetaSerDe implements IItemSerDe {

	public static final AxolotlBucketMetaSerDe INSTANCE = new AxolotlBucketMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();
	private static boolean supported;

	static {
		try {
			structure.putEntry(AXOLOTL_BUCKET_VARIANT_TAG, SerDeStructureEntry.forEnum(Variant.class));
			supported = true;
		} catch (Throwable th) {
			supported = false;
		}
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	private final Set<String> bl = new HashSet<>();
//	private final Map<String, String> relocate = new HashMap<>();
//	private final Map<String, String> reverseRelocate = new HashMap<>();

	AxolotlBucketMetaSerDe() {
		bl.add("Variant" + SPLITTER + NBTTag.INT);
//		relocate.put("Age" + SPLITTER + NBTTag.INT, getKey() + "." + AXOLOTL_BUCKET_AGE_TAG);
//		relocate.put("Health" + SPLITTER + NBTTag.FLOAT, getKey() + "." + AXOLOTL_BUCKET_HEALTH_TAG);
//		reverseRelocate.put(getKey() + "." + AXOLOTL_BUCKET_HEALTH_TAG, "Health" + SPLITTER + NBTTag.FLOAT.name());
//		reverseRelocate.put(getKey() + "." + AXOLOTL_BUCKET_AGE_TAG, "Age" + SPLITTER + NBTTag.INT);
	}

//	@Override
//	public Map<String, String> getKeysToRelocate() {
//		return Collections.unmodifiableMap(relocate);
//	}
//
//	@Override
//	public Map<String, String> getKeysToReverseRelocate() {
//		return reverseRelocate;
//	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();
		if (meta.hasVariant())
			map.put(AXOLOTL_BUCKET_VARIANT_TAG, meta.getVariant().name());
		meta.setVariant(null);
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return supported && XMaterial.AXOLOTL_BUCKET.isSupported() && item.getItemMeta() instanceof AxolotlBucketMeta;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(AXOLOTL_BUCKET_VARIANT_TAG)) {
			AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();
			meta.setVariant(Variant.valueOf((String) map.get(AXOLOTL_BUCKET_VARIANT_TAG)));
			item.setItemMeta(meta);
		}
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public String getKey() {
		return AXOLOTL_BUCKET_TAG;
	}

}
