package de.ancash.nbtnexus.serde.access;

import static de.ancash.nbtnexus.MetaTag.BOOK_AUTHOR_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_GENERATION_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_PAGES_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_TAG;
import static de.ancash.nbtnexus.MetaTag.BOOK_TITLE_TAG;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getList;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getString;

import java.util.List;
import java.util.Map;

public class BookMetaAccess extends SerializedMetaAccess {

	public BookMetaAccess() {
		super(BOOK_TAG);
	}

	public String getAuthor(Map<String, Object> map) {
		return getString(map, joinPath(BOOK_AUTHOR_TAG));
	}

	public String getTitle(Map<String, Object> map) {
		return getString(map, joinPath(BOOK_TITLE_TAG));
	}

	public List<String> getPages(Map<String, Object> map) {
		return getList(map, joinPath(BOOK_PAGES_TAG));
	}

	public String getGeneration(Map<String, Object> map) {
		return getString(map, joinPath(BOOK_GENERATION_TAG));
	}
}