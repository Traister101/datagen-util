package mod.traister101.datagenutils.data.recipe;

import com.google.errorprone.annotations.*;
import com.google.errorprone.annotations.CheckReturnValue;
import mod.traister101.datagenutils.data.util.*;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import lombok.experimental.Delegate;
import org.jetbrains.annotations.*;
import java.util.*;

/**
 * An enhanced crafting recipe builder allowing custom directory names. Also contains all factory functions for the related builders
 * like {@link #shaped(String, CraftingBookCategory, ItemLike, int)} and {@link #shapeless(String, CraftingBookCategory, ItemLike, int)}
 *
 * @param <B> The builder type. Either {@link ShapedCraftingRecipeBuilder} or {@link ShapelessCraftingRecipeBuilder}
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract sealed class CraftingRecipeBuilder<B extends CraftingRecipeBuilder<B>> extends SimpleRecipeBuilder implements RecipeBuilder {

	/**
	 * The default crafting recipe directory name
	 */
	public static final String DEFAULT_DIRECTORY = "crafting";

	/**
	 * The result stack
	 */
	protected final ItemStack result;
	/**
	 * The book category, usually {@link CraftingBookCategory#MISC}
	 */
	protected final CraftingBookCategory craftingBookCategory;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	/**
	 * The recipe group, typically not used by mods
	 */
	@Nullable
	protected String group;

	/**
	 * The constructor
	 *
	 * @param directory The directory
	 * @param craftingBookCategory The crafting book category
	 * @param result The result stack
	 */
	protected CraftingRecipeBuilder(final String directory, final CraftingBookCategory craftingBookCategory, final ItemStack result) {
		super(directory);
		this.result = result;
		this.craftingBookCategory = craftingBookCategory;
	}

	@CheckReturnValue
	@Contract("_ -> new")
	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result) {
		return shapeless(DEFAULT_DIRECTORY, result);
	}

	@CheckReturnValue
	@Contract("_, _ -> new")
	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result, final int count) {
		return shapeless(DEFAULT_DIRECTORY, CraftingBookCategory.MISC, result, count);
	}

	@CheckReturnValue
	@Contract("_, _ -> new")
	public static ShapelessCraftingRecipeBuilder shapeless(final String directory, final ItemLike result) {
		return shapeless(directory, CraftingBookCategory.MISC, result, 1);
	}

	@CheckReturnValue
	@Contract("_, _, _, _ -> new")
	public static ShapelessCraftingRecipeBuilder shapeless(final String directory, final CraftingBookCategory craftingBookCategory,
			final ItemLike result, final int count) {
		return shapeless(directory, craftingBookCategory, new ItemStack(result, count));
	}

	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static ShapelessCraftingRecipeBuilder shapeless(final String directory, final CraftingBookCategory craftingBookCategory,
			final ItemStack result) {
		return new ShapelessCraftingRecipeBuilder(directory, craftingBookCategory, result);
	}

	@CheckReturnValue
	@Contract("_ -> new")
	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result) {
		return shaped(DEFAULT_DIRECTORY, result);
	}

	@CheckReturnValue
	@Contract("_, _ -> new")
	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped(DEFAULT_DIRECTORY, CraftingBookCategory.MISC, result, count);
	}

	@CheckReturnValue
	@Contract("_, _ -> new")
	public static ShapedCraftingRecipeBuilder shaped(final String directory, final ItemLike result) {
		return shaped(directory, CraftingBookCategory.MISC, result, 1);
	}

	@CheckReturnValue
	@Contract("_, _, _, _ -> new")
	public static ShapedCraftingRecipeBuilder shaped(final String directory, final CraftingBookCategory craftingBookCategory, final ItemLike result,
			final int count) {
		return shaped(directory, craftingBookCategory, new ItemStack(result, count));
	}

	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static ShapedCraftingRecipeBuilder shaped(final String directory, final CraftingBookCategory craftingBookCategory,
			final ItemStack result) {
		return new ShapedCraftingRecipeBuilder(directory, craftingBookCategory, result);
	}

	@Override
	@CanIgnoreReturnValue
	public B unlockedBy(final String criterionName, final Criterion<?> criterion) {
		criteria.put(criterionName, criterion);
		return self();
	}

	@Override
	@CanIgnoreReturnValue
	public B group(@Nullable final String groupName) {
		group = groupName;
		return self();
	}

	@Override
	@CanIgnoreReturnValue
	public Item getResult() {
		return result.getItem();
	}

	@Override
	protected ResourceLocation getDefaultRecipeId() {
		return SimpleRecipeBuilder.getDefaultRecipeId(this.getResult());
	}

	/**
	 * Makes sure that this recipe is valid and obtainable.
	 */
	@Override
	protected void ensureValid(final ResourceLocation recipeId) {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	@Override
	protected @Nullable AdvancementBuilder makeAdvancement(final AdvancementBuilder advancement) {
		criteria.forEach(advancement::addCriterion);
		return advancement;
	}

	protected abstract B self();

	/**
	 * A recipe builder for vanilla {@link ShapedRecipe}s
	 */
	@CanIgnoreReturnValue
	public static final class ShapedCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapedCraftingRecipeBuilder> implements
			ShapedRecipeBuilder<ShapedCraftingRecipeBuilder> {

		@Delegate(excludes = ShapedPatternBuilder.Exclusions.class)
		private final ShapedPatternBuilder<ShapedCraftingRecipeBuilder> patternBuilder = new ShapedPatternBuilder<>(this);
		private boolean showNotification = true;

		public ShapedCraftingRecipeBuilder(final String folderName, final CraftingBookCategory craftingBookCategory, final ItemStack result) {
			super(folderName, craftingBookCategory, result);
		}

		public ShapedCraftingRecipeBuilder showNotification(final boolean showNotification) {
			this.showNotification = showNotification;
			return self();
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			patternBuilder.validate(recipeId);
		}

		@Override
		protected ShapedCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected ShapedRecipe recipe() {
			return new ShapedRecipe(group == null ? "" : group, craftingBookCategory, patternBuilder.build(), result, showNotification);
		}
	}

	/**
	 * A recipe builder for vanilla {@link ShapelessRecipe}s
	 */
	@CanIgnoreReturnValue
	public static final class ShapelessCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapelessCraftingRecipeBuilder> {

		private final NonNullList<Ingredient> ingredients = NonNullList.create();

		private ShapelessCraftingRecipeBuilder(final String folderName, final CraftingBookCategory craftingBookCategory, final ItemStack result) {
			super(folderName, craftingBookCategory, result);
		}

		/**
		 * Adds an ingredient that can be any item in the given tag.
		 *
		 * @param tag The tag
		 *
		 * @return This
		 */
		@Contract("_ -> this")
		public ShapelessCraftingRecipeBuilder requires(final TagKey<Item> tag) {
			return requires(Ingredient.of(tag));
		}

		/**
		 * Adds the given tag as an ingredient multiple times.
		 *
		 * @param tag The item tag
		 * @param quantity The quantity required
		 *
		 * @return This
		 */
		@Contract("_, _ -> this")
		public ShapelessCraftingRecipeBuilder requires(final TagKey<Item> tag, final int quantity) {
			return requires(Ingredient.of(tag), quantity);
		}

		/**
		 * Adds an ingredient of the given item.
		 *
		 * @param item The item
		 *
		 * @return This
		 */
		@Contract("_ -> this")
		public ShapelessCraftingRecipeBuilder requires(final ItemLike item) {
			return requires(Ingredient.of(item));
		}

		/**
		 * Adds the given item as an ingredient multiple times.
		 *
		 * @param item The item
		 * @param quantity The quantity required
		 *
		 * @return This
		 */
		@Contract("_, _ -> this")
		public ShapelessCraftingRecipeBuilder requires(final ItemLike item, final int quantity) {
			return requires(Ingredient.of(item), quantity);
		}

		/**
		 * Adds an ingredient multiple times.
		 *
		 * @param ingredient The ingredient
		 * @param quantity The quantity required
		 *
		 * @return This
		 */
		@Contract("_, _ -> this")
		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(ingredient);

			return this;
		}

		/**
		 * Adds an ingredient.
		 *
		 * @param ingredient The ingredient to require
		 *
		 * @return This
		 */
		@Contract("_ -> this")
		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			if (ingredients.isEmpty()) throw new IllegalStateException("Recipe must have at least 1 ingredient");
		}

		@Override
		protected ShapelessCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected ShapelessRecipe recipe() {
			return new ShapelessRecipe(group == null ? "" : group, craftingBookCategory, result, ingredients);
		}
	}
}