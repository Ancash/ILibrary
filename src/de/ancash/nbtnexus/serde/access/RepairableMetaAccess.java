package de.ancash.nbtnexus.serde.access;

import static de.ancash.nbtnexus.MetaTag.REPAIRABLE_REPAIR_COST_TAG;
import static de.ancash.nbtnexus.MetaTag.REPAIRABLE_TAG;
import static de.ancash.nbtnexus.serde.access.MapAccessUtil.getInt;

import java.util.Map;

public class RepairableMetaAccess extends SerializedMetaAccess {

	public RepairableMetaAccess() {
		super(REPAIRABLE_TAG);
	}

	public int getRepairCost(Map<String, Object> map) {
		return getInt(map, joinPath(REPAIRABLE_REPAIR_COST_TAG));
	}
}