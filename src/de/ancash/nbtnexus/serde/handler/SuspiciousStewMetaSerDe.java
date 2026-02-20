package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMBIENT_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMPLIFIER_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_DURATION_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_ICON_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_PARTICLES_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.SUSPICIOUS_STEW_EFFECTS_TAG;
import static de.ancash.nbtnexus.MetaTag.SUSPICIOUS_STEW_TAG;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.ancash.nbtnexus.serde.structure.SerDeStructureKeySuggestion;
import de.ancash.nbtnexus.serde.structure.SerDeStructureValueSuggestion;

public class SuspiciousStewMetaSerDe implements IItemSerDe {

	public static final SuspiciousStewMetaSerDe INSTANCE = new SuspiciousStewMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(SUSPICIOUS_STEW_EFFECTS_TAG, NBTTag.COMPOUND);
		SerDeStructure effects = structure.getList(SUSPICIOUS_STEW_EFFECTS_TAG);
		effects.putEntry(POTION_EFFECT_AMPLIFIER_TAG, SerDeStructureEntry.INT);
		effects.putEntry(POTION_EFFECT_DURATION_TAG, SerDeStructureEntry.INT);
		effects.putEntry(POTION_EFFECT_TYPE_TAG,
				new SerDeStructureEntry(new SerDeStructureKeySuggestion<String>(NBTTag.STRING, s -> PotionEffectType.getByName(s) != null),
						new SerDeStructureValueSuggestion<>(Arrays.asList(PotionEffectType.values()).stream().filter(p -> p != null)
								.map(pet -> new ValueSuggestion<>(StringHandler.INSTANCE, pet.getName(), pet.getName()))
								.collect(Collectors.toList()))));
		effects.putEntry(POTION_EFFECT_SHOW_ICON_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(POTION_EFFECT_SHOW_PARTICLES_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(POTION_EFFECT_AMBIENT_TAG, SerDeStructureEntry.BOOLEAN);
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	@SuppressWarnings("nls")
	private static final Set<String> bl = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList("Effects" + NBTNexus.SPLITTER + NBTTag.LIST + NBTNexus.SPLITTER + NBTTag.COMPOUND)));

	SuspiciousStewMetaSerDe() {
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
		if (meta.hasCustomEffects()) {
			map.put(SUSPICIOUS_STEW_EFFECTS_TAG,
					meta.getCustomEffects().stream().map(ItemSerializer.INSTANCE::serializePotionEffect).collect(Collectors.toList()));
			meta.clearCustomEffects();
		}
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return XMaterial.SUSPICIOUS_STEW.isSupported() && item.getItemMeta() instanceof SuspiciousStewMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(SUSPICIOUS_STEW_EFFECTS_TAG)) {
			SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
			((List<Map<String, Object>>) map.get(SUSPICIOUS_STEW_EFFECTS_TAG)).stream().map(ItemDeserializer.INSTANCE::deserializePotionEffect)
					.forEach(e -> meta.addCustomEffect(e, true));
			item.setItemMeta(meta);
		}
	}

	@Override
	public String getKey() {
		return SUSPICIOUS_STEW_TAG;
	}

}
