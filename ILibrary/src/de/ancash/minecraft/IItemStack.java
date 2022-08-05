package de.ancash.minecraft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.nbt.NBTItem;

@SuppressWarnings("nls")
public class IItemStack {

	private final ItemStack original;
	private final ItemStack withoutNBT;

	private final String base64original;
	private final Map<String, Object> nbtValues;
	private final int hash;

	public IItemStack(ItemStack original) {
		NBTItem check = new NBTItem(original);
		if (check.hasKey("ILibrary-temp-texture")) {
			check.removeKey("ILibrary-temp-texture");
			original = check.getItem();
		}
		this.original = original.clone();
		Duplet<ItemStack, Map<String, Object>> duplet = split(this.original.clone());
		this.withoutNBT = duplet.getFirst();
		this.withoutNBT.setAmount(1);
		this.nbtValues = duplet.getSecond();
		ItemStack temp = this.original.clone();
		if (nbtValues.containsKey("SkullOwner")) {
			try {
				String txt = ItemStackUtils.getTexure(temp);
				if (txt != null && !txt.isEmpty()) {
					NBTItem item = new NBTItem(temp);
					item.setString("ILibrary-temp-texture", txt);
					item.removeKey("SkullOwner");
					item.removeKey("skull-owner");
					temp = ItemStackUtils.setTexture(item.getItem(), null);
				}
			} catch (Exception ex) {
			}
		}
		this.base64original = ItemStackUtils.itemStackArrayToBase64(new ItemStack[] { temp });
		hash = Objects.hash(nbtValues, withoutNBT);
	}

	IItemStack(String data) throws IOException {
		this(ItemStackUtils.itemStackArrayFromBase64(data)[0]);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof IItemStack) && !(obj instanceof ItemStack))
			return false;
		if (obj instanceof ItemStack)
			return isSimilar((ItemStack) obj);
		return isSimilar((IItemStack) obj);
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

	private static Duplet<ItemStack, Map<String, Object>> split(ItemStack original) {
		HashMap<String, Object> nbtValues = new HashMap<>();
		NBTItem nbt = new NBTItem(original.clone());
		Set<String> keys = nbt.getKeys().stream().collect(Collectors.toSet());
		keys.forEach(key -> nbtValues.put(key, nbt.getObject(key)));
		if (keys.contains("SkullOwner") || keys.contains("skull-owner")) {
			try {
				String txt = ItemStackUtils.getTexure(original);
				if (txt != null && !txt.isEmpty()) {

					nbtValues.put("ILibrary-temp-texture", txt);
					nbtValues.put("SkullOwner", null);
				}
			} catch (Exception ex) {
			}
		}
		for (String key : keys)
			nbt.removeKey(key);
		return Tuple.of(nbt.getItem(), nbtValues);
	}
}