package de.ancash.minecraft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.minecraft.nbt.NBT;
import de.ancash.minecraft.nbt.NBTContainer;
import de.ancash.minecraft.nbt.iface.ReadWriteNBT;

@SuppressWarnings("nls")
public class IItemStack {

	private final ItemStack original;
	private final ItemStack withoutNBT;

	private final String base64original;
	private final Map<String, Object> nbtValues;
	private final int hash;

	public IItemStack(ItemStack original) {
		this.original = original.clone();
		ReadWriteNBT nbt = NBT.itemStackToNBT(original.clone());
		if (nbt.hasTag("ILibrary-temp-texture")) {
			nbt.removeKey("ILibrary-temp-texture");
			original = NBT.itemStackFromNBT(nbt);
		}
		Duplet<ItemStack, Map<String, Object>> duplet = split(this.original.clone());
		this.withoutNBT = duplet.getFirst();
		withoutNBT.setAmount(1);
		this.nbtValues = duplet.getSecond();
		ItemStack temp = this.original.clone();
		if (temp.getItemMeta() instanceof SkullMeta) {
			try {
				String txt = ItemStackUtils.getTexure(temp);
				if (txt != null && !txt.isEmpty()) {
					nbt = NBT.itemStackToNBT(temp);
					nbt.setString("ILibrary-temp-texture", txt);
					nbt.removeKey("SkullOwner");
					nbt.removeKey("skull-owner");
					temp = ItemStackUtils.setTexture(NBT.itemStackFromNBT(nbt), null);
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

	private static Duplet<ItemStack, Map<String, Object>> split(ItemStack item) {
		item.setAmount(1);
		HashMap<String, Object> nbtValues = new HashMap<>();

		if (item.getItemMeta() instanceof SkullMeta) {
			try {
				String txt = ItemStackUtils.getTexure(item);
				if (txt != null) {
					item = ItemStackUtils.setGameProfileId(item, new UUID(txt.hashCode(), txt.hashCode()));
				}
			} catch (Exception e) {

			}

		}

		NBTContainer nbt = (NBTContainer) NBT.itemStackToNBT(item);
		Set<String> keys = nbt.getKeys().stream().collect(Collectors.toSet());
		keys.forEach(key -> nbtValues.put(key, nbt.getObject(key)));
		if (keys.contains("SkullOwner") || keys.contains("skull-owner")) {
			try {
				String txt = ItemStackUtils.getTexure(item);
				if (txt != null && !txt.isEmpty()) {

					nbtValues.put("ILibrary-temp-texture", txt);
					nbtValues.put("SkullOwner", null);
				}
			} catch (Exception ex) {
			}
		}
		for (String key : keys) {
			if ("Count".equals(key)) {
				nbtValues.remove(key);
				continue;
			} else if ("id".equals(key)) {
				continue;
			}
			nbt.removeKey(key);
		}
		return Tuple.of(NBT.itemStackFromNBT(nbt), nbtValues);
	}
}