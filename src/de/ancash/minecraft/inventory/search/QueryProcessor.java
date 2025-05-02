package de.ancash.minecraft.inventory.search;

import java.util.List;

@FunctionalInterface
public interface QueryProcessor {

	List<QueryResult> processSearchQuery(String in);

}
