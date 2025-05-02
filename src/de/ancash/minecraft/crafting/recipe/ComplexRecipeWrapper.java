package de.ancash.minecraft.crafting.recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.tr7zw.nbtapi.utils.MinecraftVersion;

public class ComplexRecipeWrapper extends ShapedRecipe implements WrappedRecipe {

	private static final boolean USE_NAMESPACEDKEY = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_12_R1);
	private static final AtomicInteger COUNTER = new AtomicInteger();

	@SuppressWarnings("nls")
	public static ComplexRecipeWrapper newInstance(ItemStack item, ComplexRecipeType type) {
		if (!USE_NAMESPACEDKEY)
			return new ComplexRecipeWrapper(item, type);
		return new ComplexRecipeWrapper(new NamespacedKey(ILibrary.getInstance(),
				"complex-recipe-" + type.name().toLowerCase().replace("_", "-") + "-" + COUNTER.incrementAndGet()), item, type);
	}

	protected final ComplexRecipeType type;

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

	public Set<XMaterial> getIgnoredMaterials() {
		return type.getIgnoredMaterials();
	}

	protected static ItemStack updateAmount(ItemStack item, ComplexRecipeType type) {
		if (type == ComplexRecipeType.BANNER_DUPLICATE)
			item.setAmount(2);
		return item;
	}

	public enum ComplexRecipeType {
		BANNER_DUPLICATE, ARMOR_DYE, SHULKER_DYE, FIREWORK, BOOK_DUPLICATE(XMaterial.WRITTEN_BOOK), REPAIR;

		private final Set<XMaterial> ignoreOnCraft;

		private ComplexRecipeType(XMaterial... ignore) {
			ignoreOnCraft = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ignore)));
		}

		public Set<XMaterial> getIgnoredMaterials() {
			return ignoreOnCraft;
		}

		@SuppressWarnings("nls")
		public static ComplexRecipeType matchType(Object irecipe, ItemStack result) {
			String name = XMaterial.matchXMaterial(result).toString().toLowerCase(Locale.ENGLISH);
			if (name.contains("banner"))
				return BANNER_DUPLICATE;
			else if (name.contains("firework"))
				return FIREWORK;
			else if (name.contains("leather"))
				return ARMOR_DYE;
			else if (name.contains("book"))
				return BOOK_DUPLICATE;
			else if (name.contains("shulker"))
				return SHULKER_DYE;

			if (irecipe.getClass().getCanonicalName().contains("Repair"))
				return REPAIR;

			throw new IllegalArgumentException("Could not match complex recipe: \nresult name=" + name + "\nclass=" + irecipe.getClass()
					+ "\nsuper class=" + irecipe.getClass().getSuperclass());
		}
	}
}
