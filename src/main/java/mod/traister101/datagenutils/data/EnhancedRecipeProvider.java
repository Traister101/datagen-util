package mod.traister101.datagenutils.data;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An enhanced recipe provider accepting additional recipes via {@link AdditionalRecipeProvider}
 */
public abstract class EnhancedRecipeProvider extends RecipeProvider {

	private final AdditionalRecipeProvider[] additionalRecipeProviders;

	protected EnhancedRecipeProvider(final PackOutput output, final CompletableFuture<Provider> registries,
			final AdditionalRecipeProvider... additionalRecipeProviders) {
		super(output, registries);
		this.additionalRecipeProviders = additionalRecipeProviders;
	}

	@Override
	protected void buildRecipes(final RecipeOutput recipeOutput) {
		super.buildRecipes(recipeOutput);
		Arrays.stream(additionalRecipeProviders)
				.flatMap(AdditionalRecipeProvider::additionalRecipes)
				.forEach(additionalRecipe -> recipeOutput.accept(additionalRecipe.id(), additionalRecipe.value(), null));
	}

	public interface AdditionalRecipeProvider {

		Stream<? extends RecipeHolder<?>> additionalRecipes();
	}
}