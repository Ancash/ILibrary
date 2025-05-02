package de.ancash.nbtnexus.serde;

import static de.ancash.nbtnexus.MetaTag.AMOUNT_TAG;
import static de.ancash.nbtnexus.MetaTag.BLUE_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_LEVEL_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_COLORS_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_FADE_COLORS_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_FLICKER_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_TRAIL_TAG;
import static de.ancash.nbtnexus.MetaTag.FIREWORK_EFFECT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.GREEN_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_CENTER_X_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_CENTER_Z_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_LOCKED_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_SCALE_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_TRACKING_POSITION_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_UNLIMITED_TRACKING_TAG;
import static de.ancash.nbtnexus.MetaTag.MAP_VIEW_WORLD_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMBIENT_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_AMPLIFIER_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_DURATION_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_ICON_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_SHOW_PARTICLES_TAG;
import static de.ancash.nbtnexus.MetaTag.POTION_EFFECT_TYPE_TAG;
import static de.ancash.nbtnexus.MetaTag.PROPERTY_NAME_TAG;
import static de.ancash.nbtnexus.MetaTag.PROPERTY_SIGNATURE_TAG;
import static de.ancash.nbtnexus.MetaTag.PROPERTY_VALUE_TAG;
import static de.ancash.nbtnexus.MetaTag.RED_TAG;
import static de.ancash.nbtnexus.MetaTag.XMATERIAL_TAG;
import static de.ancash.nbtnexus.NBTNexus.SPLITTER_REGEX;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.nbtnexus.MetaTag;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTNexusItem;
import de.ancash.nbtnexus.NBTNexusItem.Type;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.handler.ArmorMetaSerDe;
import de.ancash.nbtnexus.serde.handler.AxolotlBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BannerMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BundleMetaSerDe;
import de.ancash.nbtnexus.serde.handler.CompassMetaSerDe;
import de.ancash.nbtnexus.serde.handler.CrossbowMetaSerDe;
import de.ancash.nbtnexus.serde.handler.DamageableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.EnchantmentStorageMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkEffectMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkMetaSerDe;
import de.ancash.nbtnexus.serde.handler.KnowledgeBookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.LeatherArmorMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MapMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MusicInstrumentMetaSerDe;
import de.ancash.nbtnexus.serde.handler.OminousBottleMetaSerDe;
import de.ancash.nbtnexus.serde.handler.PotionMetaSerDe;
import de.ancash.nbtnexus.serde.handler.RepairableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SkullMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SpawnEggMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SuspiciousStewMetaSerDe;
import de.ancash.nbtnexus.serde.handler.TropicalFishBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.UnspecificMetaSerDe;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import de.tr7zw.nbtapi.utils.MinecraftVersion;

@SuppressWarnings("deprecation")
public class ItemDeserializer {

	public static final ItemDeserializer INSTANCE = new ItemDeserializer();

	private final Set<IItemSerDe> itemDeserializer = new HashSet<>();

	ItemDeserializer() {
		itemDeserializer.add(AxolotlBucketMetaSerDe.INSTANCE);
		itemDeserializer.add(BannerMetaSerDe.INSTANCE);
		itemDeserializer.add(BookMetaSerDe.INSTANCE);
		itemDeserializer.add(BundleMetaSerDe.INSTANCE);
		itemDeserializer.add(CompassMetaSerDe.INSTANCE);
		itemDeserializer.add(FireworkEffectMetaSerDe.INSTANCE);
		itemDeserializer.add(FireworkMetaSerDe.INSTANCE);
		itemDeserializer.add(KnowledgeBookMetaSerDe.INSTANCE);
		itemDeserializer.add(LeatherArmorMetaSerDe.INSTANCE);
		itemDeserializer.add(MapMetaSerDe.INSTANCE);
		itemDeserializer.add(MusicInstrumentMetaSerDe.INSTANCE);
		itemDeserializer.add(PotionMetaSerDe.INSTANCE);
		itemDeserializer.add(UnspecificMetaSerDe.INSTANCE);
		itemDeserializer.add(SkullMetaSerDe.INSTANCE);
		itemDeserializer.add(SpawnEggMetaSerDe.INSTANCE);
		itemDeserializer.add(SuspiciousStewMetaSerDe.INSTANCE);
		itemDeserializer.add(TropicalFishBucketMetaSerDe.INSTANCE);
		itemDeserializer.add(DamageableMetaSerDe.INSTANCE);
		itemDeserializer.add(RepairableMetaSerDe.INSTANCE);
		itemDeserializer.add(EnchantmentStorageMetaSerDe.INSTANCE);
		itemDeserializer.add(CrossbowMetaSerDe.INSTANCE);
		itemDeserializer.add(ArmorMetaSerDe.INSTANCE);
		itemDeserializer.add(OminousBottleMetaSerDe.INSTANCE);
	}

	public void registerDeserializer(IItemSerDe des) {
		itemDeserializer.add(des);
	}

	public Map<String, Object> deserializeYaml(String s) {
		return deserializeYaml(YamlConfiguration.loadConfiguration(new StringReader(s)));
	}

	public Color deserializeColor(Map<String, Object> map) {
		return Color.fromRGB((int) map.get(RED_TAG), (int) map.get(GREEN_TAG), (int) map.get(BLUE_TAG));
	}

	public Map<Enchantment, Integer> deserializeEnchantments(List<Map<String, Object>> enchs) {
		return enchs.stream().map(this::deserializeEnchantment).collect(Collectors.toMap(d -> d.getFirst(), d -> d.getSecond()));
	}

	public Duplet<Enchantment, Integer> deserializeEnchantment(Map<String, Object> ench) {
		Optional<XEnchantment> match = XEnchantment.matchXEnchantment((String) ench.get(ENCHANTMENT_TYPE_TAG));
		if (!match.isPresent())
			return Tuple.of(Enchantment.getByKey(deserializeNamespacedKey((String) ench.get(ENCHANTMENT_TYPE_TAG))),
					(int) ench.get(ENCHANTMENT_LEVEL_TAG));

		return Tuple.of(match.get().getEnchant(), (int) ench.get(ENCHANTMENT_LEVEL_TAG));
	}

	public MapView deserializeMapView(Map<String, Object> map) {
		MapView view = Bukkit.createMap(Bukkit.getWorld((String) map.get(MAP_VIEW_WORLD_TAG)));
		view.setCenterX((int) map.get(MAP_VIEW_CENTER_X_TAG));
		view.setCenterZ((int) map.get(MAP_VIEW_CENTER_Z_TAG));
		view.setScale(Scale.valueOf((String) map.get(MAP_VIEW_SCALE_TAG)));
		view.setLocked((boolean) map.get(MAP_VIEW_LOCKED_TAG));
		view.setTrackingPosition((boolean) map.get(MAP_VIEW_TRACKING_POSITION_TAG));
		view.setUnlimitedTracking((boolean) map.get(MAP_VIEW_UNLIMITED_TRACKING_TAG));
		return view;
	}

	@SuppressWarnings("unchecked")
	public PropertyMap deserializePropertyMap(Map<String, Object> map) {
		PropertyMap pm = new PropertyMap();
		for (Entry<String, Object> e : map.entrySet())
			pm.putAll(e.getKey(), ((List<Map<String, Object>>) e.getValue()).stream().map(this::deserializeProperty).collect(Collectors.toList()));
		return pm;
	}

	public Property deserializeProperty(Map<String, Object> map) {
		return new Property((String) map.get(PROPERTY_NAME_TAG), (String) map.get(PROPERTY_VALUE_TAG), (String) map.get(PROPERTY_SIGNATURE_TAG));
	}

	@SuppressWarnings({ "nls" })
	public NamespacedKey deserializeNamespacedKey(String s) {
		return new NamespacedKey(s.split(":")[0], s.split(":")[1]);
	}

	public PotionEffect deserializePotionEffect(Map<String, Object> effect) {
		return new PotionEffect(PotionEffectType.getByName((String) effect.get(POTION_EFFECT_TYPE_TAG)), (int) effect.get(POTION_EFFECT_DURATION_TAG),
				(int) effect.get(POTION_EFFECT_AMPLIFIER_TAG), (boolean) effect.get(POTION_EFFECT_AMBIENT_TAG),
				(boolean) effect.get(POTION_EFFECT_SHOW_PARTICLES_TAG), (boolean) effect.get(POTION_EFFECT_SHOW_ICON_TAG));
	}

	private Map<String, Object> deserializeYaml(ConfigurationSection cs) {
		Map<String, Object> map = new HashMap<>();
		for (String key : cs.getKeys(false))
			if (cs.isConfigurationSection(key))
				map.put(key, deserializeYaml(cs.getConfigurationSection(key)));
			else
				map.put(key, cs.get(key));
		return map;
	}

	@SuppressWarnings("unchecked")
	public FireworkEffect deserializeFireworkEffect(Map<String, Object> map) {
		return FireworkEffect.builder().trail((boolean) map.get(FIREWORK_EFFECT_TRAIL_TAG)).flicker((boolean) map.get(FIREWORK_EFFECT_FLICKER_TAG))
				.with(FireworkEffect.Type.valueOf((String) map.get(FIREWORK_EFFECT_TYPE_TAG)))
				.withColor(((List<Map<String, Object>>) map.get(FIREWORK_EFFECT_COLORS_TAG)).stream().map(ItemDeserializer.INSTANCE::deserializeColor)
						.collect(Collectors.toList()))
				.withFade(((List<Map<String, Object>>) map.get(FIREWORK_EFFECT_FADE_COLORS_TAG)).stream()
						.map(ItemDeserializer.INSTANCE::deserializeColor).collect(Collectors.toList()))
				.build();
	}

	@SuppressWarnings("unchecked")
	public ItemStack deserializeItemStack(Map<String, Object> map) {
		map = (Map<String, Object>) new HashMap<>(map);
		Map<String, Object> nexus = (Map<String, Object>) map.get(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG);
		if (nexus.get(NBTNexusItem.NBT_NEXUS_ITEM_TYPE_TAG).equals(Type.SERIALIZED.name()))
			map.remove(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG);
		Optional<XMaterial> opt = XMaterial.matchXMaterial((String) map.remove(XMATERIAL_TAG));
		if (!opt.isPresent())
			throw new IllegalArgumentException();
		ItemStack item = opt.get().parseItem();
		item.setAmount((int) map.remove(AMOUNT_TAG));
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		Entry<String, Object> e = null;
		Set<String> remove = new HashSet<>();

		while (iter.hasNext()) {
			e = iter.next();
			for (IItemSerDe itd : itemDeserializer)
				if (itd.getKey().equals(e.getKey())) {
//					if (itd.hasKeysToReverseRelocate()) {
//						 relocate(map, itd.getKeysToReverseRelocate());
//					}
					itd.deserialize(item, (Map<String, Object>) map.get(e.getKey()));
					remove.add(e.getKey());
				}
		}
		remove.forEach(map::remove);
		ReadWriteNBT nbt = NBT.itemStackToNBT(item);
		if (!map.isEmpty()) {
			ReadWriteNBT customData;
			if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
				ReadWriteNBT components = nbt.getOrCreateCompound("components");
				if (map.containsKey(MetaTag.COMPONENTS)) {
					deserialize(components, (Map<String, Object>) map.remove(MetaTag.COMPONENTS));
				}
				customData = components.getOrCreateCompound("custom_data");
			} else {
				customData = nbt;
			}

			deserialize(customData, map);
		}
		return NBT.itemStackFromNBT(nbt);
	}

//	@SuppressWarnings({ "nls", "unchecked" })
//	private void relocate(Map<String, Object> map, Map<String, String> relocate) {
//		for(Entry<String, String> reloc : relocate.entrySet()) {
//			String[] keys = reloc.getKey().split("\\.");
//			Map<String, Object> tempMap = map;
//			Object orig = null;
//			for(int i = 0; i<keys.length; i++) {
//				if(tempMap.containsKey(keys[i])) {
//					orig = tempMap.get(keys[i]);
//					if(orig instanceof Map)
//						tempMap = (Map<String, Object>) orig;
//					else if(i != keys.length - 1) {
//						tempMap = null;
//						break;
//					}
//				} else{
//					tempMap = null;
//				}
//			}
//			if(tempMap == null)
//				continue;
//			orig = tempMap.remove(keys[keys.length - 1]);
//			tempMap = map;
//			keys = reloc.getValue().split("\\.");
//			for(int i = 0; i<keys.length; i++) {
//				if(i == keys.length - 1) {
//					tempMap.put(keys[i], orig);
//					break;
//				}
//				if((!tempMap.containsKey(keys[i]) || !(tempMap.get(keys[i]) instanceof Map)))
//					tempMap.put(keys[i], new HashMap<>());
//				tempMap = (Map<String, Object>) tempMap.get(keys[i]);
//			}
//		}
//	}

	public ItemStack deserializeYamlToItemStack(String s) {
		return deserializeItemStack(deserializeYaml(s));
	}

	private void deserialize(ReadWriteNBT compound, Map<String, Object> map) {
		for (String key : map.keySet())
			deserialize(compound, map, key);
	}

	@SuppressWarnings("nls")
	private void deserialize(ReadWriteNBT compound, Map<String, Object> map, String fullKey) {
		try {
			deserialize0(compound, map, fullKey);
		} catch (Exception ex) {
			throw new IllegalStateException("Could not deserialize key " + fullKey + ", value: " + map.get(fullKey) + "; map:" + map, ex);
		}
	}

	@SuppressWarnings({ "unchecked", "nls" })
	private void deserialize0(ReadWriteNBT compound, Map<String, Object> map, String fullKey) {
		if (map.get(fullKey) == null)
			return;
		String[] keys = fullKey.split(SPLITTER_REGEX);
		String field = keys[0];
		if (keys.length < 2)
			throw new IllegalArgumentException("invalid key " + fullKey);

		NBTTag tag = NBTTag.valueOf(keys[1]);

		if (tag == NBTTag.ITEM_STACK_ARRAY) {
			List<Map<?, ?>> mapList = (List<Map<?, ?>>) map.get(fullKey);
			ItemStack[] itemArr = new ItemStack[mapList.size()];
			for (int i = 0; i < itemArr.length; i++)
				itemArr[i] = deserializeItemStack((Map<String, Object>) mapList.get(i));
			compound.setItemStackArray(field, itemArr);
			return;
		}

		if (tag == NBTTag.ITEM_STACK) {
			compound.setItemStack(field, deserializeItemStack((Map<String, Object>) map.get(fullKey)));
			return;
		}

		if (tag == NBTTag.UUID) {
			compound.setUUID(field, UUID.fromString((String) map.get(fullKey)));
			return;
		}

		if (tag == NBTTag.ITEM_STACK_LIST) {
			ReadWriteNBTCompoundList list = compound.getCompoundList(field);
			List<Map<String, Object>> items = (List<Map<String, Object>>) map.get(fullKey);
			items.stream().map(this::deserializeItemStack).forEach(i -> {
				NBTContainer temp = new NBTContainer();
				temp.setItemStack(field, i);
				list.addCompound().mergeCompound(temp.getCompound(field));
			});
			return;
		}

		if (keys.length == 2) {
			if (tag == NBTTag.COMPOUND) {
				if (((Map<String, Object>) map.get(fullKey)).isEmpty())
					return;
				createNBTCompound(compound, (Map<String, Object>) map.get(fullKey), fullKey);
			} else
				set(compound, field, tag, map.get(fullKey));
		} else {
			deserializeList(compound, map, fullKey);
		}
	}

	@SuppressWarnings({ "nls" })
	private void deserializeList(ReadWriteNBT compound, Map<String, Object> src, String fullKey) {
		String[] keys = fullKey.split(SPLITTER_REGEX);
		NBTTag listType = NBTTag.valueOf(keys[2]);
		switch (listType) {
		case COMPOUND:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case STRING:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case DOUBLE:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case INT:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case FLOAT:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case LONG:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case INT_ARRAY:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		case OBJECT:
			deserializeList0(compound, src, fullKey, src.get(fullKey));
			break;
		default:
			throw new UnsupportedOperationException(listType + " list not supported");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<NBTTag> getListTypes(List list) {
		return (Set<NBTTag>) list.stream().map(Object::getClass).map(c -> NBTTag.getByClazz((Class<?>) c)).filter(i -> i != null)
				.collect(Collectors.toSet());
	}

	@SuppressWarnings({ "unchecked", "nls", "rawtypes" })
	private void deserializeList0(ReadWriteNBT compound, Map<String, Object> src, String fullKey, Object val) {
		String[] keys = fullKey.split(SPLITTER_REGEX);
		String field = keys[0];
		List list = (List) val;
		Set<NBTTag> types = getListTypes(list);
		if (types.isEmpty())
			return;
		if (types.size() != 1)
			throw new IllegalStateException("nbt list tags must be of same type, not: " + types);
		NBTTag listType = NBTTag.valueOf(keys[2]);
		NBTTag actual = types.stream().findAny().get();
		switch (listType) {
		case COMPOUND:
			ReadWriteNBTCompoundList compoundList = compound.getCompoundList(field);
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) val;

			for (Map<String, Object> temp : mapList) {
				if (temp.containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG)) {
					compound.setItemStackArray(field, mapList.stream().map(this::deserializeItemStack).toArray(ItemStack[]::new));
					return;
				}
			}

			for (Map<?, ?> temp : mapList) {
				Map<String, Object> map = (Map<String, Object>) temp;
				writeToCompound(compoundList.addCompound(), map);
			}
			break;
		case STRING:
			ReadWriteNBTList<String> stringList = compound.getStringList(field);
			stringList.addAll((Collection<String>) val);
			break;
		case DOUBLE:
			ReadWriteNBTList<Double> dList = compound.getDoubleList(field);
			dList.addAll((Collection<Double>) val);
			break;
		case INT:
			ReadWriteNBTList<Integer> iList = compound.getIntegerList(field);
			iList.addAll((Collection<Integer>) val);
			break;
		case FLOAT:
			ReadWriteNBTList<Float> fList = compound.getFloatList(field);
			((Collection<Number>) val).stream().map(Number::floatValue).forEach(f -> fList.add(f));
			break;
		case LONG:
			ReadWriteNBTList<Long> lList = compound.getLongList(field);
			((Collection<Number>) val).stream().map(Number::longValue).forEach(l -> lList.add(l));
			break;
		case INT_ARRAY:
			ReadWriteNBTList<int[]> iaList = compound.getIntArrayList(field);
			for (List<Integer> arr : (List<List<Integer>>) val)
				iaList.add(arr.stream().mapToInt(Integer::valueOf).toArray());
			break;
		case OBJECT:
			deserializeList0(compound, src,
					String.join(NBTNexus.SPLITTER, String.join(NBTNexus.SPLITTER, Arrays.copyOfRange(keys, 0, 2)), actual.name()), val);
			break;
		case LIST:
			throw new UnsupportedOperationException("nested lists not supported");
		default:
			throw new UnsupportedOperationException(listType + " list not supported");
		}
	}

	private void createNBTCompound(ReadWriteNBT parent, Map<String, Object> map, String fullKey) {
		if (map.containsKey(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG))
			parent.setItemStack(fullKey.split(SPLITTER_REGEX)[0], deserializeItemStack(map));
		else {
			writeToCompound(parent.getOrCreateCompound(fullKey.split(SPLITTER_REGEX)[0]), map);
		}
	}

	private void writeToCompound(ReadWriteNBT to, Map<String, Object> map) {
		for (String s : map.keySet())
			deserialize(to, map, s);
	}

	@SuppressWarnings("unchecked")
	private void set(ReadWriteNBT compound, String key, NBTTag type, Object value) {
		if (type == NBTTag.END)
			return;
		switch (type) {
		case BOOLEAN:
			compound.setBoolean(key, (boolean) value);
			break;
		case BYTE:
			compound.setByte(key, ((Number) value).byteValue());
			break;
		case BYTE_ARRAY:
			byte[] arr = new byte[((List<Number>) value).size()];
			for (int i = 0; i < arr.length; i++)
				arr[i] = ((List<Number>) value).get(i).byteValue();
			compound.setByteArray(key, arr);
			break;
		case DOUBLE:
			compound.setDouble(key, ((Number) value).doubleValue());
			break;
		case FLOAT:
			compound.setFloat(key, ((Number) value).floatValue());
			break;
		case INT:
			compound.setInteger(key, ((Number) value).intValue());
			break;
		case LONG:
			compound.setLong(key, ((Number) value).longValue());
			break;
		case SHORT:
			compound.setShort(key, ((Number) value).shortValue());
			break;
		case STRING:
			compound.setString(key, (String) value);
			break;
		case INT_ARRAY:
			int[] intArr = new int[((List<Number>) value).size()];
			for (int i = 0; i < intArr.length; i++)
				intArr[i] = ((List<Number>) value).get(i).intValue();
			compound.setIntArray(key, intArr);
			break;
		case COMPOUND:
			compound.mergeCompound((NBTCompound) value);
			break;
		default:
			throw new UnsupportedOperationException(type.name());
		}
	}

	public ItemStack deserializeJsonToItemStack(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		JsonObject obj = reader.readObject();
		YamlConfiguration yaml = new YamlConfiguration();
		add(obj, yaml);
		return deserializeYamlToItemStack(yaml.saveToString());
	}

	private void add(JsonObject obj, ConfigurationSection cs) {
		for (String key : obj.keySet()) {
			JsonValue val = obj.get(key);
			switch (val.getValueType()) {
			case ARRAY:
				cs.set(key, toList((JsonArray) val));
				break;
			case FALSE:
				cs.set(key, false);
				break;
			case NULL:
				break;
			case NUMBER:
				cs.set(key, ((JsonNumber) val).numberValue());
				break;
			case OBJECT:
				add((JsonObject) val, cs.createSection(key));
				break;
			case STRING:
				cs.set(key, ((JsonString) val).getChars());
				break;
			case TRUE:
				cs.set(key, true);
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("nls")
	private Object match(JsonValue val) {
		switch (val.getValueType()) {
		case ARRAY:
			return toList((JsonArray) val);
		case FALSE:
			return false;
		case NULL:
			return null;
		case NUMBER:
			return ((JsonNumber) val).numberValue();
		case OBJECT:
			JsonObject obj = (JsonObject) val;
			Map<String, Object> map = new HashMap<>();
			for (String key : obj.keySet())
				map.put(key, match(obj.get(key)));
			return map;
		case STRING:
			return ((JsonString) val).getString();
		case TRUE:
			return true;
		default:
			throw new IllegalArgumentException("null type: " + val);
		}
	}

	private List<?> toList(JsonArray array) {
		return array.stream().map(this::match).filter(t -> t != null).collect(Collectors.toList());
	}
}
