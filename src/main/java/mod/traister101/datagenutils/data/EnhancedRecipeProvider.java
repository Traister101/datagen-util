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

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param registries The registries
	 * @param additionalRecipeProviders Additional recipe providers. These aren't very common and must be run before this provider
	 */
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

	/**
	 * A bare-bones interface for providers with additional recipes
	 */
	public interface AdditionalRecipeProvider {

		/**
		 * The way we access the additional recipes
		 *
		 * @return A stream of recipe holders.
		 *
		 * @implSpec Both the id and value need non-null values. {@link RecipeHolder#id()} is used as is, and {@link RecipeHolder#value()} is encoded
		 * directly
		 */
		Stream<? extends RecipeHolder<?>> additionalRecipes();
	}
}