package de.ancash.nbtnexus.serde;

import static de.ancash.nbtnexus.MetaTag.AMOUNT_TAG;
import static de.ancash.nbtnexus.MetaTag.ATTRIBUTES_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENTS_TAG;
import static de.ancash.nbtnexus.MetaTag.ITEM_FLAGS_TAG;
import static de.ancash.nbtnexus.MetaTag.UNSPECIFIC_META_TAG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;

import de.ancash.nbtnexus.MetaTag;
import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.access.MapAccessUtil;
import de.ancash.nbtnexus.serde.comparator.DefaultSerializedItemComparator;

public class SerializedItem {

	protected static final Set<String> ignoreKey = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(AMOUNT_TAG)));
	protected static final Set<String> ignoreOrder = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(String.join(NBTNexus.SPLITTER, UNSPECIFIC_META_TAG, ENCHANTMENTS_TAG),
					String.join(NBTNexus.SPLITTER, UNSPECIFIC_META_TAG, ITEM_FLAGS_TAG),
					String.join(NBTNexus.SPLITTER, UNSPECIFIC_META_TAG, ATTRIBUTES_TAG))));

	public static SerializedItem of(ItemStack item) {
		return of(ItemSerializer.INSTANCE.serializeItemStack(item));
	}

	public static SerializedItem of(Map<String, Object> map) {
		return of(map, true);
	}

	public static SerializedItem of(Map<String, Object> map, boolean immutable) {
		return new SerializedItem(map, immutable);
	}

	@SuppressWarnings({ "unchecked", "nls" })
	private static List<String> getKeyPaths(Map<String, Object> m, String curPath) {
		List<String> paths = new ArrayList<>();
		for (Entry<String, Object> entry : m.entrySet()) {
			String path = String.join(".", curPath, entry.getKey());
			if (path.startsWith("."))
				path.replaceFirst("\\.", "");

			if (!(entry.getValue() instanceof Map))
				paths.add(path);
			else
				paths.addAll(getKeyPaths((Map<String, Object>) entry.getValue(), path));
		}
		return paths;
	}

	private final Map<String, Object> map;
	private final boolean immutable;
	private final int keyHash;
	private final int hash;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	SerializedItem(Map<String, Object> map, boolean immutable) {
		this.immutable = immutable;

		if (immutable) {
			this.map = CopyUtil.deepCopy(map, m -> Collections.unmodifiableMap(m), l -> (ArrayList) Collections.unmodifiableList(l));
			keyHash = keyHashCode0();
			Map<String, Object> temp = new HashMap<>(map);
			temp.remove(MetaTag.AMOUNT_TAG);
			hash = temp.hashCode();
		} else {
			this.map = CopyUtil.deepCopy(map, Function.identity(), Function.identity());
			keyHash = 0;
			hash = 0;
		}
	}

	public boolean isImmutable() {
		return immutable;
	}

	@SuppressWarnings("nls")
	private int keyHashCode0() {
		return getKeyPaths(map, "").hashCode();
	}

	public int keyHashCode() {
		if (immutable)
			return keyHash;
		return keyHashCode0();
	}

	@Override
	public int hashCode() {
		if (immutable)
			return hash;
		Map<String, Object> temp = new HashMap<>(map);
		temp.remove(MetaTag.AMOUNT_TAG);
		return temp.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof SerializedItem))
			return false;
		return areEqualIgnoreAmount((SerializedItem) obj);
	}

	public Set<String> getKeys() {
		return Collections.unmodifiableSet(map.keySet());
	}

	public boolean isMap(String key) {
		return MapAccessUtil.exists(map, key) && MapAccessUtil.get(map, key) instanceof Map;
	}

	public Map<String, Object> getMap(String key) {
		return MapAccessUtil.getMap(map, key);
	}

	public Object get(String s) {
		return MapAccessUtil.get(map, s);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String s) {
		return (List<T>) get(s);
	}

	public int getInt(String s) {
		return (int) get(s);
	}

	public String getString(String s) {
		return (String) get(s);
	}

	public long getLong(String s) {
		return (long) get(s);
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public ItemStack toItem() {
		return ItemDeserializer.INSTANCE.deserializeItemStack(map);
	}

	public String toJson() throws IOException {
		return Serializer.toJson(map);
	}

	public String toYaml() throws IOException {
		return Serializer.toYaml(map);
	}

	public boolean areEqual(SerializedItem item) {
		return item.keyHashCode() == keyHashCode() && DefaultSerializedItemComparator.INSTANCE.areEqualIgnoreOrder(this, item, ignoreOrder);
	}

	public boolean areEqualIgnoreAmount(SerializedItem item) {
		return item.keyHashCode() == keyHashCode() && DefaultSerializedItemComparator.INSTANCE.areEqual(this, item, ignoreKey, ignoreOrder);
	}
}
