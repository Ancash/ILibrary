package de.ancash.minecraft.inventory.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.input.StringInputGUI;

public class SearchGUI extends IGUI {

	@SuppressWarnings("nls")
	private static final ItemStack BACKGROUND = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setDisplayname(" ").build();
	private static final int itemsPerPage = 45;
	@SuppressWarnings("nls")
	private static final ItemStack previousPage = new ItemBuilder(XMaterial.ARROW).setDisplayname("§7Previous page").build();
	@SuppressWarnings("nls")
	private static final ItemStack nextPage = new ItemBuilder(XMaterial.ARROW).setDisplayname("§7Next page").build();

	private String currentQuery;
	private final Set<QueryProcessor> queryProcessor = new HashSet<>();
	private final JavaPlugin pl;
	private final List<QueryResult> results = new ArrayList<>();
	private int page;

	public SearchGUI(JavaPlugin pl, UUID id, String title) {
		this(pl, id, title, null);
	}

	public SearchGUI(JavaPlugin pl, UUID id, String title, String defaultIn) {
		super(id, 54, title);
		this.pl = pl;
		currentQuery = defaultIn;
		if (currentQuery != null)
			computeResults();
		openMain();
	}

	public void addQueryProcessor(QueryProcessor sqp) {
		queryProcessor.add(sqp);
	}

	public String getCurrentQuery() {
		return currentQuery;
	}

	@SuppressWarnings("nls")
	private void openMain() {
		newInventory(String.format("Search: '%s'", currentQuery == null ? "" : currentQuery), size);
		for (int i = 45; i < 54; i++)
			setItem(BACKGROUND, i);
		addSearchItem();
		addPageNavigators();
		int basePos = page * itemsPerPage;
		for (int i = 0; i < 45; i++) {
			if (results.size() <= basePos + i)
				break;
			int slot = i;
			addInventoryItem(new InventoryItem(this, results.get(basePos + slot).getItem(), slot, (a, b, c, top) -> {
				if (!top)
					return;
				closeAll();
				Bukkit.getScheduler().runTaskLater(pl, () -> results.get(basePos + slot).onClick(), 1);
			}));
		}
		open();
	}

	public void refresh() {
		computeResults();
		openMain();
	}

	private void computeResults() {
		page = 0;
		results.clear();
		if (currentQuery != null)
			queryProcessor.stream().map(sqp -> sqp.processSearchQuery(currentQuery)).filter(f -> f != null && !f.isEmpty()).forEach(results::addAll);
	}

	private void addPageNavigators() {
		if (page > 0) {
			addInventoryItem(new InventoryItem(this, previousPage, 45, (a, b, c, top) -> {
				if (!top)
					return;
				page--;
				openMain();
			}));
		}
		if ((page + 1) * itemsPerPage < results.size()) {
			addInventoryItem(new InventoryItem(this, nextPage, 53, (a, b, c, top) -> {
				if (!top)
					return;
				page++;
				openMain();
			}));
		}
	}

	@SuppressWarnings("nls")
	private void addSearchItem() {
		addInventoryItem(new InventoryItem(this,
				Lambda.of(new ItemBuilder(XMaterial.OAK_SIGN).setDisplayname("§eInput search"))
						.execIf(ib -> currentQuery != null, ib -> ib.setLore(String.format("§7Current input: '%s'", currentQuery)))
						.map(ItemBuilder::build).get(),
				49, (a, b, c, top) -> {
					if (!top)
						return;
					StringInputGUI sig = new StringInputGUI(pl, Bukkit.getPlayer(id), q -> {
						currentQuery = q;
						computeResults();
						Bukkit.getScheduler().runTaskLater(pl, this::openMain, 1);
					});
					sig.setTitle("Search");
					if (currentQuery == null)
						sig.setText("Search");
					else
						sig.setText(currentQuery);
					sig.setLeft(XMaterial.OAK_SIGN.parseItem());
					sig.start();
				}));
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		IGUIManager.remove(id);
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}

}