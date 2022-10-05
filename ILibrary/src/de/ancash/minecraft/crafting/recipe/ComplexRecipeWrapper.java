package de.ancash.minecraft.crafting.recipe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import de.ancash.ILibrary;
import de.ancash.minecraft.XMaterial;
import de.ancash.minecraft.nbt.utils.MinecraftVersion;

public class ComplexRecipeWrapper extends ShapedRecipe implements WrappedRecipe {

	private static final boolean USE_NAMESPACEDKEY = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_12_R1);
	private static final AtomicInteger COUNTER = new AtomicInteger();

	public static ComplexRecipeWrapper newInstance(ItemStack item, ComplexRecipeType type) {
		if (!USE_NAMESPACEDKEY)
			return new ComplexRecipeWrapper(item, type);
		return new ComplexRecipeWrapper(
				new NamespacedKey(ILibrary.getInstance(), "complex-recipe-" + COUNTER.incrementAndGet()), item, type);
	}

	private final ComplexRecipeType type;
	private final Set<Integer> ignoreOnCraft = new HashSet<>();

	public ComplexRecipeWrapper(NamespacedKey key, ItemStack result, ComplexRecipeType type) {
		super(key, updateAmount(result, type));
		this.type = type;
	}

	@SuppressWarnings("deprecation")
	public ComplexRecipeWrapper(ItemStack result, ComplexRecipeType type) {
		super(updateAmount(result, type));
		this.type = type;
	}

	public ComplexRecipeType getType() {
		return type;
	}

	public void computeIgnoreOnCraft() {
		switch (type) {
		case ARMOR_DYE:
			changeArmorDyeRecipe();
			break;
		case BOOK_DUPLICATE:
			changeBookDuplicateRecipe();
			break;
		case FIREWORK:
			changeFireworkRecipe();
			break;
		case BANNER_DUPLICATE:
			changeBannerDuplicateRecipe();
			break;
		default:
			throw new IllegalArgumentException("Invalid ComplexRecipeType: " + type);
		}
	}

	public Set<Integer> getIgnoreOnCraft() {
		return Collections.unmodifiableSet(ignoreOnCraft);
	}

	private void changeArmorDyeRecipe() {

	}

	private void changeBookDuplicateRecipe() {
		computeIgnoreOnCraft(getIngredientMap().entrySet().stream()
				.filter(entry -> entry.getValue() != null
						&& entry.getValue().toString().toLowerCase(Locale.ENGLISH).contains("written_book"))
				.findAny().get().getKey());
	}

	private void changeBannerDuplicateRecipe() {

	}

	private void computeIgnoreOnCraft(char c) {
		for (int a = 0; a < getShape().length; a++)
			for (int b = 0; b < getShape()[a].length(); b++)
				if (getShape()[a].charAt(b) == c)
					ignoreOnCraft.add(a * 3 + b);
	}

	private void changeFireworkRecipe() {

	}

	private static ItemStack updateAmount(ItemStack item, ComplexRecipeType type) {
		if (type == ComplexRecipeType.BANNER_DUPLICATE)
			item.setAmount(2);
		return item;
	}

	public enum ComplexRecipeType {
		BANNER_DUPLICATE, ARMOR_DYE, FIREWORK, BOOK_DUPLICATE;

		public static ComplexRecipeType matchType(ItemStack result) {
			String name = XMaterial.matchXMaterial(result).toString().toLowerCase(Locale.ENGLISH);
			if (name.contains("banner"))
				return BANNER_DUPLICATE;
			if (name.contains("firework"))
				return FIREWORK;
			if (name.contains("leather"))
				return ARMOR_DYE;
			if (name.contains("book"))
				return BOOK_DUPLICATE;
			return null;
		}
	}
}
