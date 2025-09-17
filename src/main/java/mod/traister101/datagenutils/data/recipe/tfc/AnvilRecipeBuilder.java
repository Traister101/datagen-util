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
import lombok.experimental.Accessors;
import java.util.*;

public abstract class AnvilRecipeBuilder extends SimpleRecipeBuilder {

	public static final String DEFAULT_WELDING_DIR = "welding";
	public static final String DEFAULT_WORKING_DIR = "anvil";

	protected final ItemStackProvider output;

	protected AnvilRecipeBuilder(final String directory, final ItemStackProvider output) {
		super(directory);
		this.output = output;
	}

	@CheckReturnValue
	public static AnvilWeldingRecipeBuilder welding(final ItemLike item, final int count, final ItemStackModifier... modifiers) {
		return welding(DEFAULT_WELDING_DIR, ItemStackProvider.of(new ItemStack(item, count), modifiers));
	}

	@CheckReturnValue
	public static AnvilWeldingRecipeBuilder welding(final String directory, final ItemStackProvider output) {
		return new AnvilWeldingRecipeBuilder(directory, output);
	}

	@CheckReturnValue
	public static AnvilWorkingRecipeBuilder working(final ItemLike item, final int count, final ItemStackModifier... modifiers) {
		return working(DEFAULT_WORKING_DIR, ItemStackProvider.of(new ItemStack(item, count), modifiers));
	}

	@CheckReturnValue
	public static AnvilWorkingRecipeBuilder working(final String directory, final ItemStackProvider output) {
		return new AnvilWorkingRecipeBuilder(directory, output);
	}

	@Override
	protected ResourceLocation getDefaultRecipeId() {
		return SimpleRecipeBuilder.getDefaultRecipeId(output.stack().getItem());
	}

	@Setter
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
		private Behavior bonus;

		public AnvilWeldingRecipeBuilder(final String directory, final ItemStackProvider output) {
			super(directory, output);
		}

		@CanIgnoreReturnValue
		public AnvilWeldingRecipeBuilder copper() {
			return tier(Metal.COPPER.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWeldingRecipeBuilder bronze() {
			return tier(Metal.BRONZE.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWeldingRecipeBuilder iron() {
			return tier(Metal.WROUGHT_IRON.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWeldingRecipeBuilder steel() {
			return tier(Metal.STEEL.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWeldingRecipeBuilder blackSteel() {
			return tier(Metal.BLACK_STEEL.tier());
		}

		@CanIgnoreReturnValue
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

	public static final class AnvilWorkingRecipeBuilder extends AnvilRecipeBuilder {

		private final List<ForgeRule> rules = new ArrayList<>();
		@Setter
		@Accessors(fluent = true)
		private Ingredient input;
		@Setter
		@Accessors(fluent = true)
		private int minTier = 0;
		private boolean applyForgingBonus;

		public AnvilWorkingRecipeBuilder(final String directory, final ItemStackProvider output) {
			super(directory, output);
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			if (ForgeRule.isConsistent(rules)) {
				throw new IllegalStateException(recipeId + " rules " + rules + " cannot be satisfied by any combination of steps!");
			}
		}

		@Override
		protected Recipe<?> recipe() {
			return new AnvilRecipe(input, minTier, rules, applyForgingBonus, output);
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder copper() {
			return minTier(Metal.COPPER.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder bronze() {
			return minTier(Metal.BRONZE.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder iron() {
			return minTier(Metal.WROUGHT_IRON.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder steel() {
			return minTier(Metal.STEEL.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder blackSteel() {
			return minTier(Metal.BLACK_STEEL.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder coloredSteel() {
			return minTier(Metal.RED_STEEL.tier());
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder rule(final ForgeRule rule) {
			rules.add(rule);
			return this;
		}

		@CanIgnoreReturnValue
		public AnvilWorkingRecipeBuilder applyForgingBonus() {
			applyForgingBonus = true;
			return this;
		}
	}
}