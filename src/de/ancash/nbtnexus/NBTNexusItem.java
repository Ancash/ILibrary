package de.ancash.nbtnexus;

@SuppressWarnings("nls")
public class NBTNexusItem {

	public static final String NBT_NEXUS_ITEM_PROPERTIES_TAG = "NBTNexusItem";
	public static final String NBT_NEXUS_ITEM_TYPE_TAG = "type";
	public static final String NBT_NEXUS_ITEM_OWNER_TAG = "owner";
	
	public enum Type {
		SERIALIZED, PLACEHOLDER;
	}
}
