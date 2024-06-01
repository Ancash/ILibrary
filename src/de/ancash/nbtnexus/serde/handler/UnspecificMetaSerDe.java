package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.ALTERNATE_COLOR_CODE;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTES_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_AMOUNT_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_NAME_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_OPERATION_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_SLOT_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTE_UUID_TAG;
import static de.ancash.nbtnexus.MetaTag.CUSTOM_MODEL_DATA;
import static de.ancash.nbtnexus.MetaTag.DISPLAYNAME_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENTS_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_LEVEL_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.ITEM_FLAGS_TAG;
import static de.ancash.nbtnexus.MetaTag.LOCALIZED_NAME_TAG;
import static de.ancash.nbtnexus.MetaTag.LORE_TAG;
import static de.ancash.nbtnexus.MetaTag.UNSPECIFIC_META_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import de.ancash.nbtnexus.MetaTag;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.access.SerializedMetaAccess;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;

public class UnspecificMetaSerDe extends SerializedMetaAccess implements IItemSerDe {

	public static final UnspecificMetaSerDe INSTANCE = new UnspecificMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(DISPLAYNAME_TAG, SerDeStructureEntry.STRING);
		structure.putList(LORE_TAG, NBTTag.STRING);
		structure.putEntry(LOCALIZED_NAME_TAG, SerDeStructureEntry.STRING);
		structure.putEntry(CUSTOM_MODEL_DATA, SerDeStructureEntry.INT);
		structure.putList(ENCHANTMENTS_TAG, NBTTag.COMPOUND);
		structure.putList(ITEM_FLAGS_TAG, NBTTag.STRING, SerDeStructureEntry.forEnum(ItemFlag.class));
		SerDeStructure enchs = structure.getList(ENCHANTMENTS_TAG);
		enchs.putEntry(ENCHANTMENT_LEVEL_TAG, SerDeStructureEntry.INT);
		enchs.putEntry(ENCHANTMENT_TYPE_TAG, SerDeStructureEntry.forEnum(XEnchantment.class));
		structure.putList(ATTRIBUTES_TAG, NBTTag.COMPOUND);
		SerDeStructure attr = structure.getList(ATTRIBUTES_TAG);
		attr.putEntry(ATTRIBUTE_TYPE_TAG, SerDeStructureEntry.forEnum(Attribute.class));
		attr.putEntry(ATTRIBUTE_NAME_TAG, SerDeStructureEntry.STRING);
		attr.putEntry(ATTRIBUTE_AMOUNT_TAG, SerDeStructureEntry.DOUBLE);
		attr.putEntry(ATTRIBUTE_OPERATION_TAG, SerDeStructureEntry.forEnum(Operation.class));
		attr.putEntry(ATTRIBUTE_UUID_TAG, SerDeStructureEntry.UUID);
		attr.putEntry(ATTRIBUTE_SLOT_TAG, SerDeStructureEntry.forEnum(EquipmentSlot.class));
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	UnspecificMetaSerDe() {
		super(MetaTag.UNSPECIFIC_META_TAG);
	}

	public String translateChatColor(String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++)
			if (b[i] == ChatColor.COLOR_CHAR && ChatColor.ALL_CODES.indexOf(b[i + 1]) > -1)
				b[i] = ALTERNATE_COLOR_CODE;
		return new String(b);
	}

	@SuppressWarnings({ "removal", "deprecation" })
	@Override
	public Map<String, Object> serialize(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		Map<String, Object> map = new HashMap<>();
		if (meta.hasLore()) {
			map.put(LORE_TAG, meta.getLore().stream().map(this::translateChatColor).collect(Collectors.toList()));
			meta.setLore(null);
		}
		if (meta.hasDisplayName()) {
			map.put(DISPLAYNAME_TAG, translateChatColor(meta.getDisplayName()));
			meta.setDisplayName(null);
		}
		if (meta.hasLocalizedName()) {
			map.put(LOCALIZED_NAME_TAG, translateChatColor(meta.getLocalizedName()));
			meta.setLocalizedName(null);
		}
		if (meta.hasCustomModelData()) {
			map.put(CUSTOM_MODEL_DATA, meta.getCustomModelData());
			meta.setCustomModelData(null);
		}

		if (!item.getEnchantments().isEmpty()) {
			map.put(ENCHANTMENTS_TAG, ItemSerializer.INSTANCE.serializeEnchantments(meta.getEnchants()));
			meta.getEnchants().keySet().stream().forEach(meta::removeEnchant);
		}

		if (!meta.getItemFlags().isEmpty()) {
			map.put(ITEM_FLAGS_TAG, meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
			meta.getItemFlags().forEach(meta::removeItemFlags);
		}

		if (meta.hasAttributeModifiers()) {
			List<Map<String, Object>> attributes = new ArrayList<>();
			for (Entry<Attribute, AttributeModifier> modifier : meta.getAttributeModifiers().entries()) {
				Map<String, Object> ser = new HashMap<>();
				ser.put(ATTRIBUTE_TYPE_TAG, modifier.getKey().name());
				ser.put(ATTRIBUTE_NAME_TAG, modifier.getValue().getName());
				ser.put(ATTRIBUTE_AMOUNT_TAG, modifier.getValue().getAmount());
				ser.put(ATTRIBUTE_OPERATION_TAG, modifier.getValue().getOperation().name());
				ser.put(ATTRIBUTE_UUID_TAG, modifier.getValue().getUniqueId().toString());
				if (modifier.getValue().getSlot() != null)
					ser.put(ATTRIBUTE_SLOT_TAG, modifier.getValue().getSlot().name());
				attributes.add(ser);
			}
			map.put(ATTRIBUTES_TAG, attributes);
			Arrays.stream(Attribute.values()).forEach(meta::removeAttributeModifier);
		}

		if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
//			if (meta.hasFood()) {
//				FoodComponent food = meta.getFood();
//				meta.setFood(null);
//
//				Map<String, Object> foodMap = new HashMap<String, Object>();
//				foodMap.put(MetaTag.POTION_EFFECTS_TAG, food.getEffects().stream().map(f -> {
//					Map<String, Object> e = new HashMap<String, Object>();
//					e.putAll(ItemSerializer.INSTANCE.serializePotionEffect(f.getEffect()));
//					e.put(MetaTag.FOOD_EFFECT_PROBABILITY_TAG, f.getProbability());
//					return e;
//				}).collect(Collectors.toCollection(ArrayList::new)));
//				foodMap.put(MetaTag.FOOD_NUTRITION_TAG, food.getNutrition());
//				foodMap.put(MetaTag.FOOD_SATURATION_TAG, food.getSaturation());
//				foodMap.put(MetaTag.FOOD_EAT_SECONDS_TAG, food.getEatSeconds());
//				foodMap.put(MetaTag.FOOD_CAN_ALWAYS_EAT_TAG, food.canAlwaysEat());
//				map.put(MetaTag.FOOD_COMPONENT_TAG, foodMap);
//			}
//
//			if (meta.hasTool()) {
//				ToolComponent tool = meta.getTool();
//				meta.setTool(null);
//
//				Map<String, Object> toolMap = new HashMap<String, Object>();
//				toolMap.put(MetaTag.TOOL_DEFAULT_MINING_SPEED_TAG, tool.getDefaultMiningSpeed());
//				toolMap.put(MetaTag.TOOL_DAMAGE_PER_BLOCK_TAG, tool.getDamagePerBlock());
//				toolMap.put(MetaTag.TOOL_RULES_TAG, tool.getRules().stream().map(r -> {
//					Map<String, Object> rule = new HashMap<String, Object>();
//					rule.put(MetaTag.TOOL_CORRECT_FOR_DROPS_TAG, r.isCorrectForDrops());
//					rule.put(MetaTag.TOOL_RULE_SPEED_TAG, r.getSpeed());
//					rule.put(MetaTag.TOOL_RULE_BLOCKS_TAG,
//							r.getBlocks().stream().map(XMaterial::matchXMaterial).map(Enum::name).collect(Collectors.toList()));
//					return rule;
//				}).collect(Collectors.toList()));
//				map.put(MetaTag.TOOL_COMPONENT_TAG, toolMap);
//			}
		}

		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.hasItemMeta();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		if (map.containsKey(ENCHANTMENTS_TAG)) {
			item.addUnsafeEnchantments(ItemDeserializer.INSTANCE.deserializeEnchantments((List<Map<String, Object>>) map.get(ENCHANTMENTS_TAG)));
		}

		ItemMeta meta = item.getItemMeta();
		if (map.containsKey(LORE_TAG))
			meta.setLore(((List<?>) map.get(LORE_TAG)).stream().map(String::valueOf)
					.map(s -> ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CODE, s)).collect(Collectors.toList()));
		if (map.containsKey(DISPLAYNAME_TAG))
			meta.setDisplayName(ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CODE, String.valueOf(map.get(DISPLAYNAME_TAG))));
		if (map.containsKey(LOCALIZED_NAME_TAG))
			meta.setLocalizedName(ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CODE, String.valueOf(map.get(LOCALIZED_NAME_TAG))));
		if (map.containsKey(CUSTOM_MODEL_DATA))
			meta.setCustomModelData(Integer.valueOf(String.valueOf(map.get(CUSTOM_MODEL_DATA))));

		if (map.containsKey(ITEM_FLAGS_TAG))
			((List<String>) map.get(ITEM_FLAGS_TAG)).stream().map(ItemFlag::valueOf).forEach(meta::addItemFlags);

		item.setItemMeta(meta);
		if (map.containsKey(ATTRIBUTES_TAG))
			deserializeAttributeModifiers(item, map);
	}

	@SuppressWarnings("unchecked")
	private void deserializeAttributeModifiers(ItemStack item, Map<String, Object> map) {
		ItemMeta meta = item.getItemMeta();

		for (Map<String, Object> attribute : (List<Map<String, Object>>) map.get(ATTRIBUTES_TAG)) {
			meta.addAttributeModifier(Attribute.valueOf((String) attribute.get(ATTRIBUTE_TYPE_TAG)),
					new AttributeModifier(UUID.fromString((String) attribute.get(ATTRIBUTE_UUID_TAG)), (String) attribute.get(ATTRIBUTE_NAME_TAG),
							(double) attribute.get(ATTRIBUTE_AMOUNT_TAG), Operation.valueOf((String) attribute.get(ATTRIBUTE_OPERATION_TAG)),
							attribute.containsKey(ATTRIBUTE_SLOT_TAG) ? EquipmentSlot.valueOf((String) attribute.get(ATTRIBUTE_SLOT_TAG)) : null));
		}
		item.setItemMeta(meta);
	}

	@Override
	public String getKey() {
		return UNSPECIFIC_META_TAG;
	}
}
