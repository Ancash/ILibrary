package de.ancash.nbtnexus.serde.handler;

import static de.ancash.nbtnexus.MetaTag.BOOK_AUTHOR_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_GENERATION_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_PAGES_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_TITLE_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import de.ancash.nbtnexus.NBTTag;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;

public class BookMetaSerDe implements IItemSerDe {

	public static final BookMetaSerDe INSTANCE = new BookMetaSerDe();
	private static final SerDeStructure structure = new SerDeStructure();

	static {
		structure.putEntry(BOOK_AUTHOR_TAG, SerDeStructureEntry.STRING);
		structure.putList(BOOK_PAGES_TAG, NBTTag.STRING);
		structure.putEntry(BOOK_TITLE_TAG, SerDeStructureEntry.STRING);
	}

	public SerDeStructure getStructure() {
		return (SerDeStructure) structure.clone();
	}

	BookMetaSerDe() {
	}

	@Override
	public Map<String, Object> serialize(ItemStack item) {
		Map<String, Object> map = new HashMap<>();
		BookMeta meta = (BookMeta) item.getItemMeta();
		if (meta.hasAuthor()) {
			map.put(BOOK_AUTHOR_TAG, meta.getAuthor());
			meta.setAuthor(null);
		}
		if (meta.hasPages()) {
			map.put(BOOK_PAGES_TAG, meta.getPages());
			meta.setPages(new ArrayList<>());
		}
		if (meta.hasTitle()) {
			map.put(BOOK_TITLE_TAG, meta.getTitle());
			meta.setTitle(null);
		}
		if (meta.hasGeneration()) {
			map.put(BOOK_GENERATION_TAG, meta.getGeneration().name());
			meta.setGeneration(null);
		}
		item.setItemMeta(meta);
		return map;
	}

	@Override
	public boolean isValid(ItemStack item) {
		return item.getItemMeta() instanceof BookMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserialize(ItemStack item, Map<String, Object> map) {
		BookMeta bm = (BookMeta) item.getItemMeta();
		bm.setAuthor((String) map.get(BOOK_AUTHOR_TAG));
		bm.setTitle((String) map.get(BOOK_TITLE_TAG));
		if (map.containsKey(BOOK_PAGES_TAG))
			bm.setPages((List<String>) map.get(BOOK_PAGES_TAG));
		if (map.containsKey(BOOK_GENERATION_TAG))
			bm.setGeneration(Generation.valueOf((String) map.get(BOOK_GENERATION_TAG)));
		item.setItemMeta(bm);
	}

	@Override
	public String getKey() {
		return BOOK_TAG;
	}
}
