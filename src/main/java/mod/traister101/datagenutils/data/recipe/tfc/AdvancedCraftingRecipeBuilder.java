package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.common.collect.Lists;
import com.google.errorprone.annotations.*;
import com.google.errorprone.annotations.CheckReturnValue;
import mod.traister101.datagenutils.data.recipe.*;
import mod.traister101.datagenutils.data.util.ShapedRecipePatternBuilder;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.outputs.*;

import net.minecraft.advancements.*;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import lombok.ToString;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.*;
import java.util.*;

/**
 * Similar to {@link CraftingRecipeBuilder} but for TFC's advanced crafting recipe types
 *
 * @param <B> The builder type. Either {@link AdvancedShapedRecipeBuilder} or {@link AdvancedShapelessRecipeBuilder}
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract sealed class AdvancedCraftingRecipeBuilder<B extends AdvancedCraftingRecipeBuilder<B>> extends SimpleRecipeBuilder implements
		RecipeBuilder {

	/**
	 * The result {@link ItemStackProvider}
	 */
	protected final ItemStackProvider result;
	/**
	 * The remainder {@link ItemStackProvider}s
	 */
	protected final List<ItemStackModifier> remainder = new ArrayList<>();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	/**
	 * The constructor
	 *
	 * @param directory The directory
	 * @param result The result
	 */
	protected AdvancedCraftingRecipeBuilder(final String directory, final ItemStackProvider result) {
		super(directory);
		this.result = result;
	}

	/**
	 * A helper factory
	 *
	 * @param result The result
	 * @param count The count
	 * @param modifiers The modifiers as a var arg
	 *
	 * @return A {@link AdvancedShapedRecipeBuilder}
	 *
	 * @implNote Uses the default {@value CraftingRecipeBuilder#DEFAULT_DIRECTORY} directory
	 */
	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AdvancedShapedRecipeBuilder shaped(final ItemLike result, final int count, final ItemStackModifier... modifiers) {
		return shaped(CraftingRecipeBuilder.DEFAULT_DIRECTORY, ItemStackProvider.of(new ItemStack(result, count), modifiers));
	}

	/**
	 * A helper factory
	 *
	 * @param directory The directory
	 * @param result The result
	 *
	 * @return A {@link AdvancedShapedRecipeBuilder}
	 */
	@CheckReturnValue
	@Contract("_, _ -> new")
	public static AdvancedShapedRecipeBuilder shaped(final String directory, final ItemStackProvider result) {
		return new AdvancedShapedRecipeBuilder(directory, result);
	}

	/**
	 * A helper factory
	 *
	 * @param result The result
	 * @param count The count
	 * @param modifiers The modifiers as a var arg
	 *
	 * @return A {@link AdvancedShapelessRecipeBuilder}
	 *
	 * @implNote Uses the default {@value CraftingRecipeBuilder#DEFAULT_DIRECTORY} directory
	 */
	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AdvancedShapelessRecipeBuilder shapeless(final ItemLike result, final int count, final ItemStackModifier... modifiers) {
		return shapeless(CraftingRecipeBuilder.DEFAULT_DIRECTORY, ItemStackProvider.of(new ItemStack(result, count), modifiers));
	}

	/**
	 * A helper factory
	 *
	 * @param directory The directory
	 * @param result The result
	 *
	 * @return A {@link AdvancedShapelessRecipeBuilder}
	 */
	@CheckReturnValue
	@Contract("_, _ -> new")
	public static AdvancedShapelessRecipeBuilder shapeless(final String directory, final ItemStackProvider result) {
		return new AdvancedShapelessRecipeBuilder(directory, result);
	}

	@Override
	@CanIgnoreReturnValue
	public B unlockedBy(final String criterionName, final Criterion<?> criterion) {
		criteria.put(criterionName, criterion);
		return self();
	}

	@Override
	@Deprecated
	@DoNotCall("TFC's advanced recipe types don't support groups")
	public B group(@Nullable final String group) {
		return self();
	}

	@Override
	public Item getResult() {
		return result.stack().getItem();
	}

	/**
	 * Adds a {@link ItemStackModifier} for the remainder
	 *
	 * @param stackModifier An {@link ItemStackModifier} for the remainder
	 *
	 * @return The builder object
	 */
	@CanIgnoreReturnValue
	public B remainder(final ItemStackModifier stackModifier) {
		remainder.add(stackModifier);
		return self();
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
	protected @Nullable Advancement.Builder makeAdvancement(final Builder advancement) {
		criteria.forEach(advancement::addCriterion);
		return advancement;
	}

	/**
	 * Helper to easily get the self object in a type safe way
	 *
	 * @return The self object
	 */
	protected abstract B self();

	/**
	 * Helper to add a {@link DamageCraftingRemainderModifier} remainder
	 *
	 * @return The builder object
	 */
	@CanIgnoreReturnValue
	public B damageInputs() {
		return remainder(DamageCraftingRemainderModifier.INSTANCE);
	}

	/**
	 * A recipe builder for tfc {@link AdvancedShapedRecipe}s
	 */
	@ToString
	@CanIgnoreReturnValue
	public static final class AdvancedShapedRecipeBuilder extends AdvancedCraftingRecipeBuilder<AdvancedShapedRecipeBuilder> {

		@Delegate(excludes = ShapedRecipePatternBuilder.DelegateExclusions.class)
		private final ShapedRecipePatternBuilder<AdvancedShapedRecipeBuilder> patternBuilder = new ShapedRecipePatternBuilder<>(this);
		private boolean showNotification = true;
		private int inputRow;
		private int inputColumn;

		private AdvancedShapedRecipeBuilder(final String folderName, final ItemStackProvider result) {
			super(folderName, result);
		}

		/**
		 * Sets and defines the input item
		 *
		 * @param symbol The symbol key
		 * @param tag The tag
		 * @param row The row index of the input item
		 * @param column The column index of the input item
		 *
		 * @return The builder
		 */
		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final TagKey<Item> tag, final int row, final int column) {
			return inputItem(symbol, Ingredient.of(tag), row, column);
		}

		/**
		 * Sets and defines the input item
		 *
		 * @param symbol The symbol key
		 * @param item The item
		 * @param row The row index of the input item
		 * @param column The column index of the input item
		 *
		 * @return The builder
		 */
		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final ItemLike item, final int row, final int column) {
			return inputItem(symbol, Ingredient.of(item), row, column);
		}

		/**
		 * Sets and defines the input item
		 *
		 * @param symbol The symbol key
		 * @param ingredient The ingredient
		 * @param row The row index of the input item
		 * @param column The column index of the input item
		 *
		 * @return The builder
		 */
		public AdvancedShapedRecipeBuilder inputItem(final Character symbol, final Ingredient ingredient, final int row, final int column) {
			inputRow = row;
			inputColumn = column;
			return define(symbol, ingredient);
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			patternBuilder.ensureValid(recipeId);
		}

		@Override
		protected AdvancedShapedRecipeBuilder self() {
			return this;
		}

		/**
		 * If this recipe should show a notification on unlock
		 *
		 * @param showNotification If this recipe should show a notification on unlock
		 *
		 * @return The builder
		 */
		public AdvancedShapedRecipeBuilder showNotification(final boolean showNotification) {
			this.showNotification = showNotification;
			return this;
		}

		@Override
		protected Recipe<?> recipe() {
			final var remainderProvider = remainder.isEmpty() ? null : ItemStackProvider.of(remainder.toArray(ItemStackModifier[]::new));
			return new AdvancedShapedRecipe(patternBuilder.build(), showNotification, result, Optional.ofNullable(remainderProvider), inputRow,
					inputColumn);
		}
	}

	/**
	 * A recipe builder for tfc {@link AdvancedShapelessRecipe}s
	 */
	@ToString
	@CanIgnoreReturnValue
	public static final class AdvancedShapelessRecipeBuilder extends AdvancedCraftingRecipeBuilder<AdvancedShapelessRecipeBuilder> {

		private final List<Ingredient> ingredients = Lists.newArrayList();
		@Nullable
		private Ingredient primaryIngredient;

		private AdvancedShapelessRecipeBuilder(final String folderName, final ItemStackProvider result) {
			super(folderName, result);
		}

		/**
		 * Set the primary ingredient
		 *
		 * @param primaryIngredient The primary ingredient
		 *
		 * @return This
		 */
		@Contract("_ -> this")
		public AdvancedShapelessRecipeBuilder primaryIngredient(final Ingredient primaryIngredient) {
			this.primaryIngredient = primaryIngredient;
			return this;
		}

		/**
		 * Adds an ingredient that can be any item in the given tag.
		 *
		 * @param tag The tag
		 *
		 * @return This
		 */
		@Contract("_ -> this")
		public AdvancedShapelessRecipeBuilder requires(final TagKey<Item> tag) {
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
		public AdvancedShapelessRecipeBuilder requires(final TagKey<Item> tag, final int quantity) {
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
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item) {
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
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item, final int quantity) {
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
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
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
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			if (primaryIngredient == null) {
				throw new IllegalStateException("No primary ingredient set for " + recipeId);
			}
		}

		@Override
		protected AdvancedShapelessRecipeBuilder self() {
			return this;
		}

		@Override
		protected Recipe<?> recipe() {
			assert primaryIngredient != null : "How has this happened?";
			final var remainderProvider = remainder.isEmpty() ? null : ItemStackProvider.of(remainder.toArray(ItemStackModifier[]::new));
			return new AdvancedShapelessRecipe(NonNullList.copyOf(ingredients), result, Optional.ofNullable(remainderProvider),
					Optional.of(primaryIngredient));
		}
	}
}