package de.ancash.minecraft;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.ancash.minecraft.nbt.NBTItem;
import de.ancash.minecraft.nbt.NBTType;
import de.ancash.misc.SerializationUtils;

public class SerializableItemStack implements Serializable{

	private static final long serialVersionUID = 9137112732805671873L;

	private final Map<String, Serializable> metaValues = new HashMap<>();
	private final Map<String, Serializable> nbtValues = new HashMap<>();
	private String base64;
	private transient ItemStack original;
	
	private static final BiFunction<Object, Map<?, ?>, Short> GET_SHORT = (key, map) -> (short) map.get(key);
	private static final BiFunction<Object, Map<?, ?>, String> GET_STRING = (key, map) -> (String) map.get(key);
	private static final Predicate<String> META_KEY_FILTER = key -> !"amount".equals(key) && !"durability".equals(key) && !"material".equals(key) && !"data".equals(key);
	private static final Predicate<String> NBT_KEY_FILTER = key -> !"Damage".equals(key);
	
	
	private static final Map<String, Set<XMaterial>> TEST = new HashMap<>();
	
	private static final String LOG = "LOG";
	private static final String PLANKS = "PLANKS";
	
	private static final BiConsumer<XMaterial, String> ADDER = (mat, key) -> XMaterial.matchXMaterial(mat.name()).ifPresent(m -> TEST.get(key).add(m));
	
	static {
		TEST.put(LOG, new HashSet<>());
		TEST.put(PLANKS, new HashSet<>());
		
		ADDER.accept(XMaterial.ACACIA_LOG, LOG);
		ADDER.accept(XMaterial.BIRCH_LOG, LOG);
		ADDER.accept(XMaterial.DARK_OAK_LOG, LOG);
		ADDER.accept(XMaterial.JUNGLE_LOG, LOG);
		ADDER.accept(XMaterial.OAK_LOG, LOG);
		ADDER.accept(XMaterial.SPRUCE_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_ACACIA_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_BIRCH_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_DARK_OAK_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_JUNGLE_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_OAK_LOG, LOG);
		ADDER.accept(XMaterial.STRIPPED_SPRUCE_LOG, LOG);
		
		ADDER.accept(XMaterial.ACACIA_PLANKS, PLANKS);
		ADDER.accept(XMaterial.BIRCH_PLANKS, PLANKS);
		ADDER.accept(XMaterial.DARK_OAK_PLANKS, PLANKS);
		ADDER.accept(XMaterial.JUNGLE_PLANKS, PLANKS);
		ADDER.accept(XMaterial.OAK_PLANKS, PLANKS);
		ADDER.accept(XMaterial.SPRUCE_PLANKS, PLANKS);
		
	}
	
	private static String getKeyByMaterial(XMaterial material) {
		Optional<String> opt = TEST.entrySet().stream().filter(entry -> entry.getValue().contains(material)).map(Map.Entry::getKey).findAny();
		if(opt.isPresent()) return opt.get();
		return null;
	}
	
	public static boolean areSimilar(SerializableItemStack a, SerializableItemStack b) {
		if((a == null) != (b == null)) return false;
		if(a == null) return false;
		
		Set<String> metaKeysA = a.metaValues.keySet().stream().filter(META_KEY_FILTER).collect(Collectors.toSet());
		Set<String> metaKeysB = b.metaValues.keySet().stream().filter(META_KEY_FILTER).collect(Collectors.toSet());
		if(!metaKeysA.equals(metaKeysB)) return false;
		if(metaKeysA.stream().filter(key -> !a.metaValues.get(key).equals(b.metaValues.get(key))).findAny().isPresent())
			return false;
		
		Set<String> nbtKeysA = a.nbtValues.keySet().stream().filter(NBT_KEY_FILTER).collect(Collectors.toSet());
		Set<String> nbtKeysB = b.nbtValues.keySet().stream().filter(NBT_KEY_FILTER).collect(Collectors.toSet());
		if(!nbtKeysA.equals(nbtKeysB)) return false;
		if(nbtKeysA.stream().filter(key -> !a.nbtValues.get(key).equals(b.nbtValues.get(key))).findAny().isPresent())
			return false;
		
		if(!XMaterial.matchXMaterial(GET_STRING.apply("material", a.metaValues)).get().parseMaterial().equals(XMaterial.matchXMaterial(GET_STRING.apply("material", b.metaValues)).get().parseMaterial())) {
			if(GET_SHORT.apply("durability", a.metaValues) != Short.MAX_VALUE) return false;
			
			String keyA = getKeyByMaterial(XMaterial.matchXMaterial(GET_STRING.apply("material", a.metaValues)).get());
			String keyB = getKeyByMaterial(XMaterial.matchXMaterial(GET_STRING.apply("material", b.metaValues)).get());
			if((keyA == null) != (keyB == null)) return false;
			if(keyA == null) return false;
			if(keyA != keyB) return false;
		}
		return true;
	}
	
	public SerializableItemStack(ItemStack item) {
		if(item == null) throw new NullPointerException("Item cannot be null!");
		this.original = item;
		setMetaValues(item);
		setNBTValues(item);
		setTexture(item);
		
		try {
			base64 = Base64.getEncoder().encodeToString(SerializationUtils.serializeToBytes(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Every key and value have to be the same!
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof SerializableItemStack)) return false;
		return metaValues.equals(((SerializableItemStack) o).metaValues) && nbtValues.equals(((SerializableItemStack) o).nbtValues);
	}

	/**
	 * Ignores the amount and durability/Damage.
	 * 
	 */
	public boolean equalsIgnoreAmount(SerializableItemStack s) {
		if(s == null || !(s instanceof SerializableItemStack)) return false;
		Set<String> metaKeysA = metaValues.keySet().stream().filter(key -> !"amount".equals(key) && !"durability".equals(key)).collect(Collectors.toSet());
		Set<String> metaKeysB = s.metaValues.keySet().stream().filter(key -> !"amount".equals(key) && !"durability".equals(key)).collect(Collectors.toSet());
		if(!metaKeysA.equals(metaKeysB)) return false;
		if(metaKeysA.stream().filter(key -> !metaValues.get(key).equals(s.metaValues.get(key))).findAny().isPresent())
			return false;
		
		Set<String> nbtKeysA = nbtValues.keySet().stream().filter(NBT_KEY_FILTER).collect(Collectors.toSet());
		Set<String> nbtKeysB = s.nbtValues.keySet().stream().filter(NBT_KEY_FILTER).collect(Collectors.toSet());
		if(!nbtKeysA.equals(nbtKeysB)) return false;
		if(nbtKeysA.stream().filter(key -> !nbtValues.get(key).equals(s.nbtValues.get(key))).findAny().isPresent())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "meta={" + metaValues + "}, nbt={" + nbtValues + "}";
	}
	
	public String asBase64() {
		return base64;
	}
	
	public boolean isValid() {
		return !metaValues.isEmpty();
	}
	
	public static SerializableItemStack fromBase64(String base64) throws ClassNotFoundException, IOException {
		return (SerializableItemStack) SerializationUtils.deserializeFromBytes(Base64.getDecoder().decode(base64));
	}
	
	public int getAmount() {
		return (int) metaValues.get("amount");
	}
	
	public boolean hasMeta() {
		return metaValues.containsKey("displayname") || metaValues.containsKey("lore") || metaValues.containsKey("itemflags") || metaValues.containsKey("enchantments");
	}
	
	@SuppressWarnings("deprecation")
	private void setMetaValues(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		
		metaValues.put("material", XMaterial.matchXMaterial(item.getType()) + ": " + XMaterial.matchXMaterial(item.getType()).getData());
		metaValues.put("data", XMaterial.matchXMaterial(item.getType()).getData());
		metaValues.put("durability", item.getDurability());
		metaValues.put("amount", item.getAmount());
		
		if(meta.hasEnchants())
			metaValues.put("enchantments", new ArrayList<>(meta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ": " + entry.getValue()).collect(Collectors.toList())));
		
		if(!meta.getItemFlags().isEmpty()) 
			metaValues.put("itemflags", new ArrayList<>(meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList())));
		if(meta instanceof SkullMeta && ((SkullMeta) meta).hasOwner())
			metaValues.put("owner", ((SkullMeta) meta).getOwner());
		if(meta.hasDisplayName()) metaValues.put("displayname", meta.getDisplayName());
		if(meta.hasLore()) metaValues.put("lore", new ArrayList<>(meta.getLore()));
		
		try {
			
			if(meta.hasAttributeModifiers()) {
				Map<String, ArrayList<Map<String, Object>>> attributeModifiers = new HashMap<>();
				for(Entry<Attribute, Collection<AttributeModifier>> entry : meta.getAttributeModifiers().asMap().entrySet()) {
					attributeModifiers.put(entry.getKey().name(), new ArrayList<>());
					entry.getValue().stream().forEach(am -> attributeModifiers.get(entry.getKey().name()).add(am.serialize()));
				}
				metaValues.put("AttributeModifier", (Serializable) attributeModifiers);
			}
			
		} catch(Exception ex) {metaValues.remove("AttributeModifier");}
	}
	
	private void setNBTValues(ItemStack item) {
		NBTItem nbt = new NBTItem(item);

		if(nbt.getKeys().isEmpty()) return;
		for(String key : nbt.getKeys()) {
			NBTType type = nbt.getType(key);
			if(type == null) continue;
			NBTType listType = nbt.getListType(key);
			switch (type) {
			case NBTTagByte:
				nbtValues.put(key, nbt.getByte(key));
				break;
			case NBTTagDouble:
				nbtValues.put(key, nbt.getDouble(key));
				break;
			case NBTTagFloat:
				nbtValues.put(key, nbt.getFloat(key));
				break;
			case NBTTagInt:
				nbtValues.put(key, nbt.getInteger(key));
				break;
			case NBTTagLong:
				nbtValues.put(key, nbt.getLong(key));
				break;
			case NBTTagShort:
				nbtValues.put(key, nbt.getShort(key));
				break;
			case NBTTagString:
				nbtValues.put(key, nbt.getString(key));
				break;
			case NBTTagList:
				switch (listType) {
				case NBTTagDouble:
					nbtValues.put(key, new ArrayList<>(nbt.getDoubleList(key)));
					break;
				case NBTTagFloat:
					nbtValues.put(key, new ArrayList<>(nbt.getFloatList(key)));
					break;
				case NBTTagInt:
					nbtValues.put(key, new ArrayList<>(nbt.getIntegerList(key)));
					break;
				case NBTTagString:
					nbtValues.put(key, new ArrayList<>(nbt.getStringList(key)));
					break;
				default:
					break;
				}
				break;
			case NBTTagByteArray:
				nbtValues.put(key, nbt.getByteArray(key));
				break;
			case NBTTagIntArray:
				nbtValues.put(key, nbt.getIntArray(key));
				break;
			default:
				break;
			}
		}
	}
	
	private void setTexture(ItemStack skull) {
		if(!skull.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) return;
		try {
			metaValues.put("texture", ItemStackUtils.getTexure(skull));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public ItemStack restore() {
		if(original != null) return original;
		ItemStack item = XMaterial.matchXMaterial((String) metaValues.get("material")).get().parseItem();
		item.setDurability((short) metaValues.get("durability"));
		item.setAmount((int) metaValues.get("amount"));
		ItemMeta meta = item.getItemMeta();
		if(metaValues.containsKey("displayname")) meta.setDisplayName((String) metaValues.get("displayname"));
		if(metaValues.containsKey("lore")) meta.setLore((ArrayList<String>) metaValues.get("lore"));
		
		if(metaValues.containsKey("itemflags")) 
			meta.addItemFlags(((ArrayList<String>) metaValues.get("itemflags")).stream().map(ItemFlag::valueOf).collect(Collectors.toList()).toArray(new ItemFlag[((ArrayList<String>) metaValues.get("itemflags")).size()]));
		
		if(meta instanceof SkullMeta && metaValues.containsKey("owner"))
			((SkullMeta) meta).setOwner((String) metaValues.get("owner"));
		if(metaValues.containsKey("AttributeModifier")) {
			Map<String, ArrayList<Map<String, Object>>> attributeModifiers = (Map<String, ArrayList<Map<String, Object>>>) metaValues.get("AttributeModifier");
			for(Entry<String, ArrayList<Map<String, Object>>> entry : attributeModifiers.entrySet()) {
				Attribute attribute = Attribute.valueOf(entry.getKey());
				for(Map<String, Object> attributeModifier : entry.getValue()) 
					meta.addAttributeModifier(attribute, AttributeModifier.deserialize(attributeModifier));
			}
		}
		item.setItemMeta(meta);
		if(metaValues.containsKey("enchantments")) 
			for(String ench : ((ArrayList<String>) metaValues.get("enchantments")))
				item.addUnsafeEnchantment(Enchantment.getByName(ench.split(": ")[0]), Integer.valueOf(ench.split(": ")[1]));
		
		if(metaValues.containsKey("texture")) 
			item = ItemStackUtils.setTexture(item, (String) metaValues.get("texture"));
		
		if(!nbtValues.isEmpty()) {
			NBTItem nbt = new NBTItem(item);
					       
			for(String key : nbtValues.keySet().stream().collect(Collectors.toList())) {
				Serializable value = nbtValues.get(key);
				if(value instanceof Byte) {
					nbt.setByte(key, (Byte) value);
				} else if(value instanceof Double) {
					nbt.setDouble(key, (Double) value);
				} else if(value instanceof Float) {
					nbt.setFloat(key, (Float) value);
				} else if(value instanceof Integer) {
					nbt.setInteger(key, (Integer) value);
				} else if(value instanceof Long) {
					nbt.setLong(key, (Long) value);
				} else if(value instanceof Short) {
					nbt.setShort(key, (Short) value);
				} else if(value instanceof String) {
					nbt.setString(key, (String) value);
				} else if(value instanceof ArrayList<?>) {
					nbt.setObject(key, value);
				} 
			}
			original = nbt.getItem();
		} else {
			original = item;
		}
		return original;
	}
}