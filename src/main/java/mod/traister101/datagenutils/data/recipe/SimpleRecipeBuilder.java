package mod.traister101.datagenutils.data.recipe;

import mod.traister101.datagenutils.data.util.AdvancementBuilder;

import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import lombok.AllArgsConstructor;
import javax.annotation.Nullable;

@AllArgsConstructor
public abstract class SimpleRecipeBuilder {

	/**
	 * The directory, can be empty to ignore
	 */
	private final String directory;

	/**
	 * Helper for getting a default recipe id from an {@link ItemLike}
	 *
	 * @param item The item to use for the default recipe id
	 *
	 * @return The default recipe id for this item
	 *
	 * @implNote This is just the items registry name
	 */
	protected static ResourceLocation getDefaultRecipeId(final ItemLike item) {
		return RecipeBuilder.getDefaultRecipeId(item);
	}

	/**
	 * The default recipe id for this recipe
	 *
	 * @return The default recipe id to use
	 */
	protected abstract ResourceLocation getDefaultRecipeId();

	/**
	 * Makes sure that this recipe is valid and obtainable.
	 *
	 * @param recipeId The recipe id (for more useful error reporting)
	 *
	 * @throws IllegalStateException And friends when invalid. (You shouldn't catch these, fix your code)
	 */
	protected abstract void ensureValid(ResourceLocation recipeId) throws IllegalStateException;

	/**
	 * Create the recipe object
	 *
	 * @return Create the actual recipe object
	 */
	protected abstract Recipe<?> recipe();

	/**
	 * Makes the advancement using the provided builder, if no advancement is required returns {@code null}
	 *
	 * @param builder The advancement builder, pre-populated with the standard recipe unlock trigger, reward and requirements
	 *
	 * @return A {@code null} advancement to signify no advancement is desired or an advancement builder
	 */
	@Nullable
	protected AdvancementBuilder makeAdvancement(final AdvancementBuilder builder) {
		return null;
	}

	/**
	 * Save the recipe to the provided output using the provided id (prefixed with the directory)
	 *
	 * @param recipeOutput The recipe output
	 * @param recipeId The recipe id. The directory is prepended
	 */
	public final void save(final RecipeOutput recipeOutput, final ResourceLocation recipeId) {
		ensureValid(recipeId);
		final var advancement = makeAdvancement(AdvancementBuilder.recipe(recipeId));
		final var realRecipeId = directory.isEmpty() ? recipeId : recipeId.withPrefix(directory + "/");
		recipeOutput.accept(realRecipeId, recipe(), advancement == null ? null : advancement.build(realRecipeId.withPrefix("recipes/")));
	}

	/**
	 * Save the recipe to the provided output using the default recipe id
	 *
	 * @param recipeOutput The recipe output
	 *
	 * @implNote This uses the recipes default recipe id, some builders might not be able to generate a good default id so this can potentially throw
	 */
	public final void save(final RecipeOutput recipeOutput) {
		save(recipeOutput, getDefaultRecipeId());
	}

	/**
	 * Save the recipe to the provided output using the id as specified by a string
	 *
	 * @param recipeOutput The recipe output
	 * @param id A resource string for the location, if no namespace is specified it'll default to {@value ResourceLocation#DEFAULT_NAMESPACE}
	 */
	public final void save(final RecipeOutput recipeOutput, final String id) {
		final var defaultRecipeId = getDefaultRecipeId();
		final var parsedLocation = ResourceLocation.parse(id);
		if (parsedLocation.equals(defaultRecipeId)) {
			throw new IllegalStateException("Recipe " + id + " should remove its 'save' 'id' argument as it is equal to default one");
		}
		save(recipeOutput, parsedLocation);
	}
}