package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BANNER_PATTERNS_TAG;
import static de.ancash.nbtnexus.MetaTag.BANNER_PATTERN_COLOR_TAG;
import static de.ancash.nbtnexus.MetaTag.BANNER_PATTERN_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.BANNER_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

@SuppressWarnings("unchecked")
public class BannerMetaSerDe implements IItemSerDe {

	public static final BannerMetaSerDe INSTANCE = new BannerMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(BANNER_PATTERNS_TAG, NBTTag.COMPOUND);
		SerDeStructure pattern = structure.getList(BANNER_PATTERNS_TAG);
		pattern.putEntry(BANNER_PATTERN_TYPE_TAG, SerDeStructureEntry.forEnum(PatternType.class));
		pattern.putEntry(BANNER_PATTERN_COLOR_TAG, SerDeStructureEntry.forEnum(DyeColor.class));
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	BannerMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> patterns = new ArrayList<>();
		BannerMeta meta = (BannerMeta) item.getItemMeta();
		for (Pattern pattern : meta.getPatterns()) {
			Map<String, Object> p = new HashMap<>();
			p.put(BANNER_PATTERN_TYPE_TAG, pattern.getPattern().name());
			p.put(BANNER_PATTERN_COLOR_TAG, pattern.getColor().name());
			patterns.add(p);
		}
		meta.setPatterns(new ArrayList<>());
		item.setItemMeta(meta);
		map.put(BANNER_PATTERNS_TAG, patterns);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof BannerMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		BannerMeta bm = (BannerMeta) item.getItemMeta();
		List<Map<String, Object>> patterns = (List<Map<String, Object>>) map.get(BANNER_PATTERNS_TAG);
		for (Map<String, Object> pattern : patterns)
			bm.addPattern(new Pattern(DyeColor.valueOf((String) pattern.get(BANNER_PATTERN_COLOR_TAG)),
					PatternType.valueOf((String) pattern.get(BANNER_PATTERN_TYPE_TAG))));
		item.setItemMeta(bm);
	}

	@Override
	public String getKey() {
		return BANNER_TAG;
	}
}
