package de.ancash.nbtnexus.serde.access;

import static de.ancash.nbtnexus.MetaTag.AMOUNT_TAG;
import static de.ancash.nbtnexus.MetaTag.CUSTOM_MODEL_DATA;
import static de.ancash.nbtnexus.MetaTag.DISPLAYNAME_TAG;
import static de.ancash.nbtnexus.MetaTag.ENCHANTMENTS_TAG;
import static de.ancash.nbtnexus.MetaTag.ITEM_FLAGS_TAG;
import static de.ancash.nbtnexus.MetaTag.LOCALIZED_NAME_TAG;
import static de.ancash.nbtnexus.MetaTag.LORE_TAG;
import static de.ancash.nbtnexus.MetaTag.UNSPECIFIC_META_TAG;
import static de.ancash.nbtnexus.MetaTag.XMATERIAL_TAG;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getInt;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getList;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getString;

import java.util.List;
import java.util.Map;

public class UnspecificMetaAccess extends SerializedMetaAccess {

	public UnspecificMetaAccess() {
		super(UNSPECIFIC_META_TAG);
	}

	public String getDisplayName(Map<String, Object> map) {
		return getString(map, joinPath(DISPLAYNAME_TAG));
	}

	public String getLocalizedName(Map<String, Object> map) {
		return getString(map, joinPath(LOCALIZED_NAME_TAG));
	}

	public int getCustomModelData(Map<String, Object> map) {
		return getInt(map, joinPath(CUSTOM_MODEL_DATA));
	}

	public List<String> getLore(Map<String, Object> map) {
		return getList(map, joinPath(LORE_TAG));
	}

	public int getAmount(Map<String, Object> map) {
		return getInt(map, AMOUNT_TAG);
	}

	public String getMaterial(Map<String, Object> map) {
		return getString(map, XMATERIAL_TAG);
	}

	public List<Map<String, Object>> getEnchantments(Map<String, Object> map) {
		return getList(map, joinPath(ENCHANTMENTS_TAG));
	}

	public List<String> getItemFlags(Map<String, Object> map) {
		return getList(map, joinPath(ITEM_FLAGS_TAG));
	}
}