package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.errorprone.annotations.*;
import mod.traister101.datagenutils.data.recipe.SimpleRecipeBuilder;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.WeldingRecipe.Behavior;
import net.dries007.tfc.common.recipes.outputs.*;
import net.dries007.tfc.util.Metal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import lombok.Setter;
import lombok.experimental.*;
import org.jetbrains.annotations.Contract;
import java.util.*;

/**
 * The base recipe builder for TFCs anvil recipes. Currently, these are only {@link AnvilWeldingRecipeBuilder} {@link AnvilWorkingRecipeBuilder}
 */
public abstract class AnvilRecipeBuilder extends SimpleRecipeBuilder {

	/**
	 * The default directory for welding recipes
	 */
	public static final String DEFAULT_WELDING_DIR = "welding";
	/**
	 * The default directory for working recipes
	 */
	public static final String DEFAULT_WORKING_DIR = "anvil";

	/**
	 * The output {@link ItemStackProvider}
	 */
	protected final ItemStackProvider output;

	/**
	 * The constructor
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param output The output {@link ItemStackProvider}
	 */
	protected AnvilRecipeBuilder(final String directory, final ItemStackProvider output) {
		super(directory);
		this.output = output;
	}

	/**
	 * Helper factory for {@link AnvilWeldingRecipeBuilder}
	 *
	 * @param item The item output
	 * @param count The item count
	 * @param modifiers The {@link ItemStackModifier}s
	 *
	 * @return A new {@link AnvilWeldingRecipeBuilder} using {@value DEFAULT_WELDING_DIR} for the directory
	 */
	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AnvilWeldingRecipeBuilder welding(final ItemLike item, final int count, final ItemStackModifier... modifiers) {
		return welding(DEFAULT_WELDING_DIR, ItemStackProvider.of(new ItemStack(item, count), modifiers));
	}

	/**
	 * Helper factory for {@link AnvilWeldingRecipeBuilder}
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param output The output {@link ItemStackProvider}
	 *
	 * @return A new {@link AnvilWeldingRecipeBuilder}
	 */
	@CheckReturnValue
	@Contract("_, _ -> new")
	public static AnvilWeldingRecipeBuilder welding(final String directory, final ItemStackProvider output) {
		return new AnvilWeldingRecipeBuilder(directory, output);
	}

	/**
	 * Helper factory for {@link AnvilWorkingRecipeBuilder}
	 *
	 * @param item The item output
	 * @param count The item count
	 * @param modifiers The {@link ItemStackModifier}s
	 *
	 * @return A new {@link AnvilWorkingRecipeBuilder} using {@value DEFAULT_WORKING_DIR} for the directory
	 */
	@CheckReturnValue
	@Contract("_, _, _ -> new")
	public static AnvilWorkingRecipeBuilder working(final ItemLike item, final int count, final ItemStackModifier... modifiers) {
		return working(DEFAULT_WORKING_DIR, ItemStackProvider.of(new ItemStack(item, count), modifiers));
	}

	/**
	 * Helper factory for {@link AnvilWorkingRecipeBuilder}
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param output The output {@link ItemStackProvider}
	 *
	 * @return A new {@link AnvilWorkingRecipeBuilder}
	 */
	@CheckReturnValue
	@Contract("_, _ -> new")
	public static AnvilWorkingRecipeBuilder working(final String directory, final ItemStackProvider output) {
		return new AnvilWorkingRecipeBuilder(directory, output);
	}

	@Override
	protected ResourceLocation getDefaultRecipeId() {
		return SimpleRecipeBuilder.getDefaultRecipeId(output.stack().getItem());
	}

	/**
	 * Builder for TFC's {@link WeldingRecipe}
	 */
	@Setter
	@CanIgnoreReturnValue
	@Accessors(fluent = true)
	public final static class AnvilWeldingRecipeBuilder extends AnvilRecipeBuilder {

		/**
		 * The left hand input
		 */
		private Ingredient firstInput;
		/**
		 * The right hand input
		 */
		private Ingredient secondInput;
		/**
		 * The anvil tier
		 */
		private int tier;
		/**
		 * The bonus behavior
		 */
		private Behavior bonus;

		/**
		 * The constructor
		 *
		 * @param directory The directory, can be empty to ignore
		 * @param output The output {@link ItemStackProvider}
		 */
		public AnvilWeldingRecipeBuilder(final String directory, final ItemStackProvider output) {
			super(directory, output);
		}

		/**
		 * Set the first input
		 *
		 * @param items The items for the first input
		 *
		 * @return {@code this}
		 */
		@Tolerate
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder firstInput(final ItemLike... items) {
			return firstInput(Ingredient.of(items));
		}

		/**
		 * Set the second input
		 *
		 * @param items The items for the second input
		 *
		 * @return {@code this}
		 */
		@Tolerate
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder secondInput(final ItemLike... items) {
			return secondInput(Ingredient.of(items));
		}

		/**
		 * Helper that sets the minimum tier to copper
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder copper() {
			return tier(Metal.COPPER.tier());
		}

		/**
		 * Helper that sets the minimum tier to bronze
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder bronze() {
			return tier(Metal.BRONZE.tier());
		}

		/**
		 * Helper that sets the minimum tier to iron
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder iron() {
			return tier(Metal.WROUGHT_IRON.tier());
		}

		/**
		 * Helper that sets the minimum tier to steel
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder steel() {
			return tier(Metal.STEEL.tier());
		}

		/**
		 * Helper that sets the minimum tier to black steel
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder blackSteel() {
			return tier(Metal.BLACK_STEEL.tier());
		}

		/**
		 * Helper that sets the minimum tier to colored steel (Red and Blue)
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public AnvilWeldingRecipeBuilder coloredSteel() {
			return tier(Metal.RED_STEEL.tier());
		}

		@Override
		@SuppressWarnings("ConstantValue")
		protected void ensureValid(final ResourceLocation recipeId) {
			if (firstInput == null) {
				throw new IllegalStateException(recipeId + " doesn't have it's first input assigned");
			}
			if (secondInput == null) {
				throw new IllegalStateException(recipeId + " doesn't have it's second input assigned");
			}
			if (bonus == null) {
				throw new IllegalStateException(recipeId + " doesn't have a welding behavior bonus assigned");
			}
		}

		@Override
		protected Recipe<?> recipe() {
			return new WeldingRecipe(firstInput, secondInput, tier, output, bonus);
		}
	}

	/**
	 * Builder for TFCs {@link AnvilRecipe}s (A working recipe)
	 */
	@CanIgnoreReturnValue
	public static final class AnvilWorkingRecipeBuilder extends AnvilRecipeBuilder {

		private final List<ForgeRule> rules = new ArrayList<>();
		/**
		 * The input worked item
		 */
		@Setter
		@Accessors(fluent = true)
		private Ingredient input = Ingredient.EMPTY;
		/**
		 * The minimum anvil tier required to perform the recipe
		 */
		@Setter
		@Accessors(fluent = true)
		private int minTier = 0;
		/**
		 * If the resulting item should have the forging bonus applied
		 */
		@Setter
		@Accessors(fluent = true)
		private boolean applyForgingBonus;

		/**
		 * The constructor
		 *
		 * @param directory The directory, can be empty to ignore
		 * @param output The output {@link ItemStackProvider}
		 */
		public AnvilWorkingRecipeBuilder(final String directory, final ItemStackProvider output) {
			super(directory, output);
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			if (ForgeRule.isConsistent(rules)) {
				throw new IllegalStateException(recipeId + " rules " + rules + " cannot be satisfied by any combination of steps!");
			}
			if (input.isEmpty()) throw new IllegalStateException(recipeId + " input is empty!");
		}

		@Override
		protected Recipe<?> recipe() {
			return new AnvilRecipe(input, minTier, rules, applyForgingBonus, output);
		}

		/**
		 * Helper that sets the minimum tier to copper
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder copper() {
			return minTier(Metal.COPPER.tier());
		}

		/**
		 * Helper that sets the minimum tier to bronze
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder bronze() {
			return minTier(Metal.BRONZE.tier());
		}

		/**
		 * Helper that sets the minimum tier to iron
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder iron() {
			return minTier(Metal.WROUGHT_IRON.tier());
		}

		/**
		 * Helper that sets the minimum tier to steel
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder steel() {
			return minTier(Metal.STEEL.tier());
		}

		/**
		 * Helper that sets the minimum tier to black steel
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder blackSteel() {
			return minTier(Metal.BLACK_STEEL.tier());
		}

		/**
		 * Helper that sets the minimum tier to colored steel (Red and Blue)
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "-> this", mutates = "this")
		public AnvilWorkingRecipeBuilder coloredSteel() {
			return minTier(Metal.RED_STEEL.tier());
		}

		/**
		 * Adds a {@link ForgeRule} to the builder
		 *
		 * @param rule A {@link ForgeRule}. The builder order must be consistent as determined by {@link ForgeRule#isConsistent(List)}
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = "_ -> this", mutates = "this")
		public AnvilWorkingRecipeBuilder rule(final ForgeRule rule) {
			rules.add(rule);
			return this;
		}

		/**
		 * Sets this recipe to apply a forging bonus
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		@Contract(value = " -> this", mutates = "this")
		public AnvilWorkingRecipeBuilder applyForgingBonus() {
			return applyForgingBonus(true);
		}
	}
}