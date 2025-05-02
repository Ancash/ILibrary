package de.ancash.nbtnexus.serde.access;

import static de.ancash.nbtnexus.MetaTag.DAMAGEABLE_DAMAGE_TAG;
import static de.ancash.nbtnexus.MetaTag.DAMAGEABLE_TAG;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getInt;

import java.util.Map;

public class DamageableMetaAccess extends SerializedMetaAccess {

	public DamageableMetaAccess() {
		super(DAMAGEABLE_TAG);
	}

	public int getDamage(Map<String, Object> map) {
		return getInt(map, joinPath(DAMAGEABLE_DAMAGE_TAG));
	}
}