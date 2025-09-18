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
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract sealed class AdvancedCraftingRecipeBuilder<B extends AdvancedCraftingRecipeBuilder<B>> extends SimpleRecipeBuilder implements
		RecipeBuilder {

	public static final String DEFAULT_DIRECTORY = CraftingRecipeBuilder.DEFAULT_DIRECTORY;

	protected final ItemStackProvider result;
	protected final List<ItemStackModifier> remainder = new ArrayList<>();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	protected AdvancedCraftingRecipeBuilder(final String directory, final ItemStackProvider result) {
		super(directory);
		this.result = result;
	}

	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AdvancedShapedRecipeBuilder shaped(final ItemLike result, final int count, final ItemStackModifier... modifiers) {
		return shaped(DEFAULT_DIRECTORY, ItemStackProvider.of(new ItemStack(result, count), modifiers));
	}

	@CheckReturnValue
	@Contract("_, _ -> new")
	public static AdvancedShapedRecipeBuilder shaped(final String directory, final ItemStackProvider result) {
		return new AdvancedShapedRecipeBuilder(directory, result);
	}

	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AdvancedShapelessRecipeBuilder shapeless(final ItemLike result, final int count, final ItemStackModifier... modifiers) {
		return shapeless(DEFAULT_DIRECTORY, ItemStackProvider.of(new ItemStack(result, count), modifiers));
	}

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
	 * @param stackModifier An {@link ItemStackModifier} for the remainder
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

	protected abstract B self();

	/**
	 * Adds {@link DamageCraftingRemainderModifier} when set
	 */
	@CanIgnoreReturnValue
	public B damageInputs() {
		return remainder(DamageCraftingRemainderModifier.INSTANCE);
	}

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

	@ToString
	@CanIgnoreReturnValue
	public static final class AdvancedShapelessRecipeBuilder extends AdvancedCraftingRecipeBuilder<AdvancedShapelessRecipeBuilder> {

		private final List<Ingredient> ingredients = Lists.newArrayList();
		@Nullable
		private Ingredient primaryIngredient;

		private AdvancedShapelessRecipeBuilder(final String folderName, final ItemStackProvider result) {
			super(folderName, result);
		}

		public AdvancedShapelessRecipeBuilder primaryIngredient(final Ingredient primaryIngredient) {
			this.primaryIngredient = primaryIngredient;
			return this;
		}

		/**
		 * Adds an ingredient that can be any item in the given tag.
		 */
		public AdvancedShapelessRecipeBuilder requires(final TagKey<Item> tag) {
			return requires(Ingredient.of(tag));
		}

		/**
		 * Adds an ingredient of the given item.
		 */
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item) {
			return requires(Ingredient.of(item));
		}

		/**
		 * Adds the given ingredient multiple times.
		 */
		public AdvancedShapelessRecipeBuilder requires(final ItemLike item, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(item);
			return this;
		}

		/**
		 * Adds an ingredient.
		 */
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		/**
		 * Adds an ingredient multiple times.
		 */
		public AdvancedShapelessRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
			for (int i = 0; i < quantity; ++i) requires(ingredient);

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