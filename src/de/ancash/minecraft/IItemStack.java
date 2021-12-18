package de.ancash.minecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.Multimap;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Quartet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.nbt.NBTItem;

public class IItemStack {
	
	private final ItemStack original;
	private final ItemStack withoutNBT;
	
	private final String base64original;
	private final Map<String, Object> nbtValues;
	
	public IItemStack(ItemStack original) {
		this.original = original;
		Duplet<ItemStack, Map<String, Object>> duplet = split(original);
		this.withoutNBT = duplet.getFirst();
		this.nbtValues = duplet.getSecond();
		this.base64original = ItemStackUtils.itemStackArrayToBase64(new ItemStack[] {original});
	}

	IItemStack(String data) throws IOException {
		this(ItemStackUtils.itemStackArrayFromBase64(data)[0]);
	}
	
	public static IItemStack fromBase64(String data) throws IOException {
		return new IItemStack(data);
	}
	
	public String asBase64() {
		return base64original;
	}
	
	public ItemStack getOriginal() {
		return original.clone();
	}
	
	public boolean isSimilar(ItemStack compareTo) {
		return isSimilar(new IItemStack(compareTo));
	}
	
	public boolean isSimilar(IItemStack compareTo) {
		return withoutNBT.isSimilar(compareTo.withoutNBT) && nbtValues.equals(compareTo.nbtValues);
	}
	
	public static Duplet<ItemStack, Map<String, Object>> split(ItemStack original) {
		HashMap<String, Object> nbtValues = new HashMap<>();
		NBTItem nbt = new NBTItem(original);
		ItemMeta meta = original.getItemMeta();
		if(nbt.hasKey("SkullOwner")) {
			try {
				nbtValues.put("SkullTexture", ItemStackUtils.getTexure(original));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			nbt.removeKey("SkullOwner");
		}
		if(original.getItemMeta() instanceof LeatherArmorMeta) {
			nbtValues.put("LeatherColor", ((LeatherArmorMeta) meta).getColor());
		}
		if(nbt.hasKey("AttributeModifiers")) {
			Multimap<Attribute, AttributeModifier> attributeModifiers = meta.getAttributeModifiers();
			Map<Attribute, List<Quartet<String, Double, Operation, EquipmentSlot>>> attributes = new HashMap<>();
			for(Attribute att : attributeModifiers.keySet()) {
				List<Quartet<String, Double, Operation, EquipmentSlot>> modis = new ArrayList<>();
				for(AttributeModifier modi : attributeModifiers.get(att))
					modis.add(Tuple.of(modi.getName(), modi.getAmount(), modi.getOperation(), modi.getSlot()));
				attributes.put(att, modis);
			}
			nbt.removeKey("AttributeModifiers");
			nbtValues.put("AttributeModifiers", attributes);
		}
		ItemStack split = nbt.getItem();
		meta = split.getItemMeta();
		meta.setAttributeModifiers(null);
		if(nbtValues.containsKey("LeatherColor"))
			((LeatherArmorMeta) meta).setColor(null);
		split.setItemMeta(meta);
		return Tuple.of(split, nbtValues);
	}
	
	@SuppressWarnings("unchecked")
	public static ItemStack combine(ItemStack item, Map<String, Object> nbtValues) {
		if(nbtValues.containsKey("SkullTexture"))
			item = ItemStackUtils.setTexture(item, (String) nbtValues.get("SkullTexture"));
		if(nbtValues.containsKey("LeatherColor")) {
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			lam.setColor((Color) nbtValues.get("LeatherColor"));
			item.setItemMeta(lam);
		}
		if(nbtValues.containsKey("AttributeModifier")) {
			ItemMeta meta =  item.getItemMeta();
			Map<Attribute, List<Object[]>> attributes = (Map<Attribute, List<Object[]>>) nbtValues.get("AttributeModifiers");
			for(Attribute att : attributes.keySet()) {
				for(Object[] modifier : attributes.get(att))
					meta.addAttributeModifier(att, new AttributeModifier(UUID.randomUUID(),
							(String) modifier[0],
							(int) modifier[1], 
							(Operation) modifier[2], 
							(EquipmentSlot) modifier[3]));
			}
			item.setItemMeta(meta);
		}
		return item;
	}
}