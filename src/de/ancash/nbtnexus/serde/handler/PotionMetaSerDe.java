package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BASE_POTION_EXTENDED_TAG;
import static de.ancash.nbtnexus.MetaTag.BASE_POTION_TAG;
import static de.ancash.nbtnexus.MetaTag.BASE_POTION_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.BASE_POTION_UPGRADED_TAG;
import static de.ancash.nbtnexus.MetaTag.BLUE_TAG;
import static de.ancash.nbtnexus.MetaTag.GREEN_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_COLOR_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECTS_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMBIENT_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMPLIFIER_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_DURATION_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_ICON_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_PARTICLES_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_TAG;
import static de.ancash.nbtnexus.MetaTag.RED_TAG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import de.ancash.minecraft.inventory.editor.yml.handler.StringHandler;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.ancash.nbtnexus.serde.structure.SerDeStructureKeySuggestion;
import de.ancash.nbtnexus.serde.structure.SerDeStructureValueSuggestion;

public class PotionMetaSerDe implements IItemSerDe {

	public static final PotionMetaSerDe INSTANCE = new PotionMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putMap(BASE_POTION_TAG);
		SerDeStructure bp = structure.getMap(BASE_POTION_TAG);
		bp.putEntry(BASE_POTION_TYPE_TAG, SerDeStructureEntry.forEnum(PotionType.class));
		bp.putEntry(BASE_POTION_EXTENDED_TAG, SerDeStructureEntry.BOOLEAN);
		bp.putEntry(BASE_POTION_UPGRADED_TAG, SerDeStructureEntry.BOOLEAN);
		structure.putList(POTION_EFFECTS_TAG, NBTTag.COMPOUND);
		SerDeStructure effects = structure.getList(POTION_EFFECTS_TAG);
		effects.putEntry(POTION_EFFECT_AMPLIFIER_TAG, SerDeStructureEntry.INT);
		effects.putEntry(POTION_EFFECT_DURATION_TAG, SerDeStructureEntry.INT);
		effects.putEntry(POTION_EFFECT_TYPE_TAG,
				new SerDeStructureEntry(new SerDeStructureKeySuggestion<String>(NBTTag.STRING, s -> PotionEffectType.getByName(s) != null),
						new SerDeStructureValueSuggestion<>(Arrays.asList(PotionEffectType.values()).stream()
								.map(pet -> new ValueSuggestion<>(StringHandler.INSTANCE, pet.getName(), pet.getName()))
								.collect(Collectors.toList()))));
		effects.putEntry(POTION_EFFECT_SHOW_ICON_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(POTION_EFFECT_SHOW_PARTICLES_TAG, SerDeStructureEntry.BOOLEAN);
		effects.putEntry(POTION_EFFECT_AMBIENT_TAG, SerDeStructureEntry.BOOLEAN);
		structure.putMap(POTION_COLOR_TAG);
		SerDeStructure color = structure.getMap(POTION_COLOR_TAG);
		color.putEntry(RED_TAG, SerDeStructureEntry.INT);
		color.putEntry(GREEN_TAG, SerDeStructureEntry.INT);
		color.putEntry(BLUE_TAG, SerDeStructureEntry.INT);
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	PotionMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		map.put(POTION_EFFECTS_TAG,
				meta.getCustomEffects().stream().map(ItemSerializer.INSTANCE::serializePotionEffect).collect(Collectors.toList()));
		if (((List<?>) map.get(POTION_EFFECTS_TAG)).isEmpty())
			map.remove(POTION_EFFECTS_TAG);

		PotionData potionData = meta.getBasePotionData();
		Map<String, Object> basePotion = new HashMap<>();
		basePotion.put(BASE_POTION_TYPE_TAG, potionData.getType().name());
		basePotion.put(BASE_POTION_EXTENDED_TAG, potionData.isExtended());
		basePotion.put(BASE_POTION_UPGRADED_TAG, potionData.isUpgraded());
		map.put(BASE_POTION_TAG, basePotion);

		if (meta.hasColor()) {
			map.put(POTION_COLOR_TAG, ItemSerializer.INSTANCE.serializeColor(meta.getColor()));
			meta.setColor(null);
		}
		meta.clearCustomEffects();
		item.setItemMeta(meta);
		item.setType(Material.BEDROCK);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof PotionMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if (map.containsKey(POTION_EFFECTS_TAG))
			((List<Map<String, Object>>) map.get(POTION_EFFECTS_TAG)).stream().map(ItemDeserializer.INSTANCE::deserializePotionEffect)
					.forEach(e -> meta.addCustomEffect(e, true));
		Map<String, Object> potionBase = (Map<String, Object>) map.get(BASE_POTION_TAG);
		meta.setBasePotionData(new PotionData(PotionType.valueOf((String) potionBase.get(BASE_POTION_TYPE_TAG)),
				(boolean) potionBase.get(BASE_POTION_EXTENDED_TAG), (boolean) potionBase.get(BASE_POTION_UPGRADED_TAG)));

		if (map.containsKey(POTION_COLOR_TAG))
			meta.setColor(ItemDeserializer.INSTANCE.deserializeColor((Map<String, Object>) map.get(POTION_COLOR_TAG)));

		item.setItemMeta(meta);
	}

	@Override
	public String getKey() {
		return POTION_TAG;
	}
}
