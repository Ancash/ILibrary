package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BLUE_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECTS_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_COLORS_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_FADE_COLORS_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_FLICKER_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_TRAIL_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_POWER_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_TAG;
import static de.ancash.nbtnexus.MetaTag.GREEN_TAG;
import static de.ancash.nbtnexus.MetaTag.RED_TAG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class FireworkMetaSerDe implements IItemSerDe {

	public static final FireworkMetaSerDe INSTANCE = new FireworkMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(FIREWORK_POWER_TAG, SerDeStructureEntry.INT);
		structure.putList(FIREWORK_EFFECTS_TAG, NBTTag.COMPOUND);
		SerDeStructure effects = structure.getList(FIREWORK_EFFECTS_TAG);
		effects.putEntry(FIREWORK_EFFECT_TRAIL_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(FIREWORK_EFFECT_FLICKER_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(FIREWORK_EFFECT_TYPE_TAG, SerDeStructureEntry.STRING);
		effects.putList(FIREWORK_EFFECT_COLORS_TAG, NBTTag.COMPOUND);
		SerDeStructure color = effects.getList(FIREWORK_EFFECT_COLORS_TAG);
		color.putEntry(RED_TAG, SerDeStructureEntry.INT);
		color.putEntry(GREEN_TAG, SerDeStructureEntry.INT);
		color.putEntry(BLUE_TAG, SerDeStructureEntry.INT);
		effects.putList(FIREWORK_EFFECT_FADE_COLORS_TAG, NBTTag.COMPOUND);
		SerDeStructure fcolor = effects.getList(FIREWORK_EFFECT_FADE_COLORS_TAG);
		fcolor.putEntry(RED_TAG, SerDeStructureEntry.INT);
		fcolor.putEntry(GREEN_TAG, SerDeStructureEntry.INT);
		fcolor.putEntry(BLUE_TAG, SerDeStructureEntry.INT);
	}

	private final Set<String> bl = new HashSet<>();

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	@SuppressWarnings("nls")
	FireworkMetaSerDe() {
		bl.add("Fireworks" + NBTNexus.SPLITTER + NBTTag.COMPOUND.name());
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		FireworkMeta meta = (FireworkMeta) item.getItemMeta();
		map.put(FIREWORK_POWER_TAG, meta.getPower());
		if (meta.hasEffects())
			map.put(FIREWORK_EFFECTS_TAG,
					meta.getEffects().stream().map(ItemSerializer.INSTANCE::serializeFireworkEffect).collect(Collectors.toList()));
		meta.clearEffects();
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof FireworkMeta;
	}

	@Override
	public String getKey() {
		return FIREWORK_TAG;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		FireworkMeta meta = (FireworkMeta) item.getItemMeta();
		meta.setPower((int) map.get(FIREWORK_POWER_TAG));
		if (map.containsKey(FIREWORK_EFFECTS_TAG)) {
			meta.addEffects(((List<Map<String, Object>>) map.get(FIREWORK_EFFECTS_TAG)).stream()
					.map(ItemDeserializer.INSTANCE::deserializeFireworkEffect).collect(Collectors.toList()));
		}
		item.setItemMeta(meta);
	}
}
