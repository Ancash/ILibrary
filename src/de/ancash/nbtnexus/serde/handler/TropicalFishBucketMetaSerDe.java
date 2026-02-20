package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.TROPICAL_FISH_BUCKET_BODY_COLOR_TAG;
import static de.ancash.nbtnexus.MetaTag.TROPICAL_FISH_BUCKET_PATTERN_COLOR_TAG;
import static de.ancash.nbtnexus.MetaTag.TROPICAL_FISH_BUCKET_PATTERN_TAG;
import static de.ancash.nbtnexus.MetaTag.TROPICAL_FISH_BUCKET_TAG;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class TropicalFishBucketMetaSerDe implements IItemSerDe {

	public static final TropicalFishBucketMetaSerDe INSTANCE = new TropicalFishBucketMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	private static final String PATTERN_CLAZZ = "org.bukkit.entity.TropicalFish$Pattern";
	
	static {
		try {
			Class.forName(PATTERN_CLAZZ);
			structure.putEntry(TROPICAL_FISH_BUCKET_BODY_COLOR_TAG, SerDeStructureEntry.forEnum(DyeColor.class));
			structure.putEntry(TROPICAL_FISH_BUCKET_PATTERN_COLOR_TAG, SerDeStructureEntry.forEnum(DyeColor.class));
			structure.putEntry(TROPICAL_FISH_BUCKET_PATTERN_TAG, SerDeStructureEntry.forEnum(Pattern.class));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println(PATTERN_CLAZZ + " not found -> no TropicalFishBucket support");
		}

	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	TropicalFishBucketMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();
		if (meta.hasVariant()) {
			map.put(TROPICAL_FISH_BUCKET_BODY_COLOR_TAG, meta.getBodyColor().name());
			map.put(TROPICAL_FISH_BUCKET_PATTERN_COLOR_TAG, meta.getPatternColor().name());
			map.put(TROPICAL_FISH_BUCKET_PATTERN_TAG, meta.getPattern().name());
		}
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return XMaterial.TROPICAL_FISH_BUCKET.isSupported() && item.getItemMeta() instanceof TropicalFishBucketMeta;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(TROPICAL_FISH_BUCKET_BODY_COLOR_TAG)) {
			TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();
			meta.setBodyColor(DyeColor.valueOf((String) map.get(TROPICAL_FISH_BUCKET_BODY_COLOR_TAG)));
			meta.setPatternColor(DyeColor.valueOf((String) map.get(TROPICAL_FISH_BUCKET_PATTERN_COLOR_TAG)));
			meta.setPattern(Pattern.valueOf((String) map.get(TROPICAL_FISH_BUCKET_PATTERN_TAG)));
			item.setItemMeta(meta);
		}
	}

	@Override
	public String getKey() {
		return TROPICAL_FISH_BUCKET_TAG;
	}

}
