package de.ancash.minecraft.inventory.search.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.minecraft.inventory.search.QueryProcessor;
import de.ancash.minecraft.inventory.search.QueryResult;

public class MaterialQueryProcessor implements QueryProcessor {

	private final Consumer<XMaterial> onClick;

	public MaterialQueryProcessor(Consumer<XMaterial> onClick) {
		this.onClick = onClick;
	}

	@Override
	public List<QueryResult> processSearchQuery(String in) {
		List<QueryResult> qr = new ArrayList<>();
		for (XMaterial mat : XMaterial.VALUES) {
			if (mat.toString().toLowerCase().contains(in.toLowerCase()))
				qr.add(new QueryResult(mat.parseItem(), () -> onClick.accept(mat)));
		}
		return qr;
	}

}
