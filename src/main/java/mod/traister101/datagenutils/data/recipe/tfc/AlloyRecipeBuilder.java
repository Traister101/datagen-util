package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.recipe.SimpleRecipeBuilder;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.util.AlloyRange;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;

import org.jetbrains.annotations.Contract;
import java.util.*;

/**
 * A recipe builder for TFC's {@link AlloyRecipe}
 */
public final class AlloyRecipeBuilder extends SimpleRecipeBuilder {

	/**
	 * The default directory for alloy recipes
	 */
	public static final String DEFAULT_ALLOY_DIR = "alloy";

	private final List<AlloyRange> contents = new ArrayList<>();
	private final Fluid result;

	/**
	 * The constructor
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The resulting fluid
	 */
	public AlloyRecipeBuilder(final String directory, final Fluid result) {
		super(directory);
		this.result = result;
	}

	/**
	 * Factory method
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The resulting fluid
	 *
	 * @return new {@link AlloyRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static AlloyRecipeBuilder of(final String directory, final Fluid result) {
		return new AlloyRecipeBuilder(directory, result);
	}

	/**
	 * Helper factory
	 *
	 * @param result The resulting fluid
	 *
	 * @return new {@link AlloyRecipeBuilder} with {@value DEFAULT_ALLOY_DIR} as the output directory
	 */
	@Contract("_ -> new")
	public static AlloyRecipeBuilder of(final Fluid result) {
		return new AlloyRecipeBuilder(DEFAULT_ALLOY_DIR, result);
	}

	@Override
	protected ResourceLocation getDefaultRecipeId() {
		return BuiltInRegistries.FLUID.getKey(result);
	}

	@Override
	protected void ensureValid(final ResourceLocation recipeId) {
		if (contents.isEmpty()) {
			throw new IllegalStateException(recipeId + " must have contents");
		}
	}

	@Override
	protected Recipe<?> recipe() {
		return new AlloyRecipe(contents, result);
	}

	/**
	 * Add alloy contents
	 *
	 * @param fluid The fluid
	 * @param min The minimum
	 * @param max The maximum
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@SuppressWarnings("unused")
	@Contract("_, _, _ -> this")
	public AlloyRecipeBuilder contents(final Fluid fluid, final double min, final double max) {
		return contents(new AlloyRange(fluid, min, max));
	}

	/**
	 * Add alloy contents
	 *
	 * @param alloyRange An {@link AlloyRange}
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@Contract("_ -> this")
	public AlloyRecipeBuilder contents(final AlloyRange alloyRange) {
		contents.add(alloyRange);
		return this;
	}
}