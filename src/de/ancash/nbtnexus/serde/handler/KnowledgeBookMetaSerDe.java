package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.KNOWLEDGE_BOOK_RECIPES_TAG;
import static de.ancash.nbtnexus.MetaTag.KNOWLEDGE_BOOK_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.ItemDeserializer;
import de.ancash.nbtnexus.serde.ItemSerializer;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;

public class KnowledgeBookMetaSerDe implements IItemSerDe {

	public static final KnowledgeBookMetaSerDe INSTANCE = new KnowledgeBookMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putList(KNOWLEDGE_BOOK_RECIPES_TAG, NBTTag.STRING);
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	KnowledgeBookMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
		if (meta.hasRecipes()) {
			map.put(KNOWLEDGE_BOOK_RECIPES_TAG,
					meta.getRecipes().stream().map(ItemSerializer.INSTANCE::serializeNamespacedKey).collect(Collectors.toList()));
		}
		meta.setRecipes(new ArrayList<>());
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof KnowledgeBookMeta;
	}

	@Override
	public String getKey() {
		return KNOWLEDGE_BOOK_TAG;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		KnowledgeBookMeta meta = (KnowledgeBookMeta) item.getItemMeta();
		if (map.containsKey(KNOWLEDGE_BOOK_RECIPES_TAG))
			meta.setRecipes(((List<String>) map.get(KNOWLEDGE_BOOK_RECIPES_TAG)).stream().map(ItemDeserializer.INSTANCE::deserializeNamespacedKey)
					.collect(Collectors.toList()));
		item.setItemMeta(meta);
	}
}
