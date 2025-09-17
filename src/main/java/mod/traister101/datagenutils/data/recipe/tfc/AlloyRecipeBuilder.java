package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.recipe.SimpleRecipeBuilder;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.util.AlloyRange;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public final class AlloyRecipeBuilder extends SimpleRecipeBuilder {

	public static final String DEFAULT_ALLOY_DIR = "alloy";

	private final List<AlloyRange> contents = new ArrayList<>();
	private final Fluid result;

	public AlloyRecipeBuilder(final String directory, final Fluid result) {
		super(directory);
		this.result = result;
	}

	public static AlloyRecipeBuilder of(final String directory, final Fluid result) {
		return new AlloyRecipeBuilder(directory, result);
	}

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

	@CanIgnoreReturnValue
	public AlloyRecipeBuilder contents(final AlloyRange alloyRange) {
		contents.add(alloyRange);
		return this;
	}
}