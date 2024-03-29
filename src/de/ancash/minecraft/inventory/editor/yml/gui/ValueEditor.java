package de.ancash.minecraft.inventory.editor.yml.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Tuple;
import de.ancash.lambda.Lambda;
import de.ancash.minecraft.ItemBuilder;
import de.ancash.minecraft.inventory.IGUI;
import de.ancash.minecraft.inventory.IGUIManager;
import de.ancash.minecraft.inventory.InventoryItem;
import de.ancash.minecraft.inventory.editor.yml.EditorSettings;
import de.ancash.minecraft.inventory.editor.yml.YamlEditor;
import de.ancash.minecraft.inventory.editor.yml.suggestion.ValueSuggestion;

public abstract class ValueEditor<T> extends IGUI {

	public static <T> Function<T, Duplet<Boolean, String>> isValid(YamlEditor yeditor, ValueEditor<T> ve) {
		return in -> {
			Optional<String> o = yeditor.isValid(ve, in);
			return Tuple.of(!o.isPresent(), o.orElse(null));
		};
	}

	protected final EditorSettings settings;
	protected final Supplier<T> valSup;
	protected Runnable onBack;
	protected final ValueEditor<?> parent;
	protected final YamlEditor yeditor;
	protected final String key;
	protected int suggestionsPos = 0;
	protected final List<ValueSuggestion<T>> suggestions;

	public ValueEditor(UUID id, String title, int size, ValueEditor<?> parent, YamlEditor yeditor, String key, Supplier<T> valSup, Runnable onBack) {
		super(id, size, title);
		this.settings = yeditor.getSettings();
		this.key = key;
		this.yeditor = yeditor;
		this.onBack = onBack;
		this.parent = parent;
		this.valSup = valSup;
		for (int i = 0; i < getSize(); i++)
			setItem(settings.getBackgroundItem(), i);
		if (onBack != null)
			addInventoryItem(new InventoryItem(this, settings.getBackItem(), getSize() - 5, (a, b, c, top) -> Lambda.execIf(top, this::back)));
		if (!(this instanceof ConfigurationSectionEditor))
			suggestions = yeditor.getValueSuggester().stream().map(ivs -> ivs.getValueSuggestions(this)).filter(s -> s != null && !s.isEmpty())
					.flatMap(Set::stream).sorted((a, b) -> a.getAbbreviation().compareTo(b.getAbbreviation())).collect(Collectors.toList());
		else
			suggestions = new ArrayList<>();
		open();
	}

	public ConfigurationSectionEditor getClosesConfigurationSectionEditor() {
		if (this instanceof ConfigurationSectionEditor)
			return (ConfigurationSectionEditor) this;
		if (!hasParent())
			return null;
		return getParent().getClosesConfigurationSectionEditor();
	}

	public List<ValueSuggestion<T>> getValueSuggestions() {
		return suggestions;
	}

	public String getKey() {
		return key;
	}

	public boolean hasKey() {
		return key != null;
	}

	public ValueEditor<?> getParent() {
		return parent;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	protected void addEditorItemWithSuggestions(int slot, XMaterial mat) {
		if (suggestions.isEmpty())
			return;
		StringBuilder lore = new StringBuilder();
		lore.append("§7Value: ").append(valSup.get().toString()).append("\n").append("§eMouse wheel to select type").append("\n")
				.append("§eShift click to skip 10").append("\n").append("§eRight/Left click to add property").append("\n").append("§eSuggestions:")
				.append("\n");
		StringBuilder sugs = new StringBuilder();
		for (int i = suggestionsPos + suggestions.size() - 1; i >= suggestionsPos; i--) {
			if (i == suggestionsPos) {
				sugs.append("§a, ").append("§a");
			} else {
				sugs.append("§f, ").append("§f");
			}
			sugs.append(suggestions.get(i % suggestions.size()).getAbbreviation());
		}
		lore.append(sugs.toString().replaceFirst(", ", "").replaceAll("(.{1,140})\\s+", "$1\n"));
		addInventoryItem(new InventoryItem(this, new ItemBuilder(mat).setDisplayname("").setLore(lore.toString().split("\n")).build(), slot,
				(sl, shift, action, top) -> {
					if (!top)
						return;
					if (shift) {
						if (!suggestions.isEmpty()) {
							suggestionsPos = (suggestionsPos + 10) % suggestions.size();
							addEditorItemWithSuggestions(slot, mat);
						}
						return;
					}
					switch (action) {
					case PICKUP_ALL:
						useSuggestion(suggestions.get(suggestionsPos));
						break;
					case PICKUP_HALF:
						nextSuggestion();
						addEditorItemWithSuggestions(slot, mat);
						break;
					default:
						break;
					}
				}));
	}

	protected abstract void saveListElement(Object val);

	protected abstract void useSuggestion(ValueSuggestion<T> sugg);

	protected void nextSuggestion() {
		if (suggestions.isEmpty())
			return;
		suggestionsPos = (suggestionsPos + 1) % suggestions.size();
	}

	protected ItemStack getEditorItem() {
		return new ItemBuilder(XMaterial.REDSTONE_TORCH).setDisplayname(String.valueOf(valSup.get())).build();
	}

	protected void back() {
		closeAll();
		onBack.run();
	}

	public YamlEditor getYamlEditor() {
		return yeditor;
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
