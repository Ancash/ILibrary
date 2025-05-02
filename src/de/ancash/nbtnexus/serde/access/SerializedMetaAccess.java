package de.ancash.nbtnexus.serde.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public abstract class SerializedMetaAccess {

	public static final UnspecificMetaAccess UNSPECIFIC_META_ACCESS = new UnspecificMetaAccess();

	protected final String key;

	protected SerializedMetaAccess(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public boolean isValid(Map<String, Object> map) {
		return map.containsKey(key);
	}

	public boolean exists(Map<String, Object> map, String key) {
		return MapAccessUtil.exists(map, key);
	}

	@SuppressWarnings("nls")
	protected String joinPath(String... str) {
		return String.join(".",
				Arrays.asList(new String[] { key }, str).stream().map(Arrays::asList).flatMap(Collection::stream).toArray(String[]::new));
	}
}
