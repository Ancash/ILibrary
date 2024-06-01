package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.GAME_PROFILE_ID_TAG;
import static de.ancash.nbtnexus.MetaTag.GAME_PROFILE_NAME_TAG;
import static de.ancash.nbtnexus.MetaTag.GAME_PROFILE_PROPERTIES_TAG;
import static de.ancash.nbtnexus.MetaTag.GAME_PROFILE_TAG;
import static de.ancash.nbtnexus.MetaTag.SKULL_NOTE_BLOCK_SOUND_TAG;
import static de.ancash.nbtnexus.MetaTag.SKULL_TAG;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.cryptomorin.xseries.XMaterial;

import de.ancash.minecraft.AuthLibUtil;
import de.ancash.minecraft.ItemStackUtils;
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
import de.tr7zw.nbtapi.utils.MinecraftVersion;

@SuppressWarnings({ "nls", "unchecked" })
public class SkullMetaSerDe implements IItemSerDe {

	public static final SkullMetaSerDe INSTANCE = new SkullMetaSerDe();
	private static Field gameProfileField;
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(SKULL_NOTE_BLOCK_SOUND_TAG, SerDeStructureEntry.STRING);
		structure.putMap(GAME_PROFILE_TAG);
		SerDeStructure gp = structure.getMap(GAME_PROFILE_TAG);
		gp.putEntry(GAME_PROFILE_ID_TAG, SerDeStructureEntry.UUID);
		gp.putEntry(GAME_PROFILE_NAME_TAG, SerDeStructureEntry.STRING);
		structure.putMap(GAME_PROFILE_PROPERTIES_TAG);
		SerDeStructure prop = structure.getMap(GAME_PROFILE_PROPERTIES_TAG);
		prop.putList("textures", NBTTag.COMPOUND);
		SerDeStructure texture = prop.getList("textures");
		texture.putEntry("Value", SerDeStructureEntry.STRING);
		texture.putEntry("Name", new SerDeStructureEntry(SerDeStructureKeySuggestion.STRING,
				new SerDeStructureValueSuggestion<String>(new ValueSuggestion<String>(StringHandler.INSTANCE, "textures", "textures"))));
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	private static final Set<String> bl = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList("SkullOwner" + NBTNexus.SPLITTER + NBTTag.COMPOUND)));

	static {
		try {
			gameProfileField = ((SkullMeta) XMaterial.PLAYER_HEAD.parseItem().getItemMeta()).getClass().getDeclaredField("profile");
			gameProfileField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	SkullMetaSerDe() {
	}

	@Override
	public Set<String> getBlacklistedKeys() {
		return bl;
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		GameProfile gp = null;
		try {
			gp = ItemStackUtils.getGameProfile(meta);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		if (gp != null) {
			Map<String, Object> gps = (Map<String, Object>) map.computeIfAbsent(GAME_PROFILE_TAG, k -> new HashMap<>());
			if (gp.getId() != null)
				gps.put(GAME_PROFILE_ID_TAG, gp.getId().toString());
			if (gp.getName() != null)
				gps.put(GAME_PROFILE_NAME_TAG, gp.getName());
			map.put(GAME_PROFILE_PROPERTIES_TAG, ItemSerializer.INSTANCE.serialzePropertyMap(gp.getProperties()));
			try {
				gameProfileField.set(meta, null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_19_R2) && meta.getNoteBlockSound() != null) {
			map.put(SKULL_NOTE_BLOCK_SOUND_TAG, ItemSerializer.INSTANCE.serializeNamespacedKey(meta.getNoteBlockSound()));
		}
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof SkullMeta;
	}

	@Override
	public String getKey() {
		return SKULL_TAG;
	}

	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (map.containsKey(GAME_PROFILE_TAG)) {
			Map<String, Object> gps = (Map<String, Object>) map.get(GAME_PROFILE_TAG);
			GameProfile gp = null;
			if (gps.containsKey(GAME_PROFILE_ID_TAG))
				gp = AuthLibUtil.createGameProfile(UUID.fromString((String) gps.get(GAME_PROFILE_ID_TAG)), (String) gps.get(GAME_PROFILE_NAME_TAG));
			else
				gp = AuthLibUtil.createGameProfile(null, (String) gps.get(GAME_PROFILE_NAME_TAG));

			if (map.containsKey(GAME_PROFILE_PROPERTIES_TAG))
				gp.getProperties()
						.putAll(ItemDeserializer.INSTANCE.deserializePropertyMap((Map<String, Object>) map.get(GAME_PROFILE_PROPERTIES_TAG)));
			try {
				gameProfileField.set(meta, gp);
				if (gps.containsKey(GAME_PROFILE_ID_TAG))
					ItemStackUtils.setGameProfileId(meta, UUID.fromString((String) gps.get(GAME_PROFILE_ID_TAG)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}
		if (map.containsKey(SKULL_NOTE_BLOCK_SOUND_TAG))
			meta.setNoteBlockSound(ItemDeserializer.INSTANCE.deserializeNamespacedKey((String) map.get(SKULL_NOTE_BLOCK_SOUND_TAG)));
		item.setItemMeta(meta);
	}
}
