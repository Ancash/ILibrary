package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.DAMAGEABLE_DAMAGE_TAG;
import static de.ancash.nbtnexus.MetaTag.DAMAGEABLE_TAG;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class DamageableMetaSerDe implements IItemSerDe {

	public static final DamageableMetaSerDe INSTANCE = new DamageableMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(DAMAGEABLE_DAMAGE_TAG, SerDeStructureEntry.INT);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	@SuppressWarnings("nls")
	private static final Set<String> bl = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Damage" + NBTNexus.SPLITTER + NBTTag.INT)));

	DamageableMetaSerDe() {
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		Damageable meta = (Damageable) item.getItemMeta();
		if (meta.hasDamage()) {
			map.put(DAMAGEABLE_DAMAGE_TAG, meta.getDamage());
		}
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof Damageable;
	}

	@Override
	public String getKey() {
		return DAMAGEABLE_TAG;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		Damageable meta = (Damageable) item.getItemMeta();
		if (map.containsKey(DAMAGEABLE_DAMAGE_TAG))
			meta.setDamage((int) map.get(DAMAGEABLE_DAMAGE_TAG));
		item.setItemMeta(meta);
	}
}
