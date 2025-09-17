package mod.traister101.datagenutils.data.recipe;

import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import lombok.AllArgsConstructor;
import javax.annotation.Nullable;

@AllArgsConstructor
public abstract class SimpleRecipeBuilder {

	private final String directory;

	/**
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
	 * @return Create the actual recipe object
	 */
	protected abstract Recipe<?> recipe();

	/**
	 * @param advancement The advancement builder, pre-populated with the standard recipe unlock trigger, reward and requirements
	 *
	 * @return A {@code null} advancement to signify no advancement is desired or an advancement builder
	 */
	@Nullable
	protected Builder makeAdvancement(final Builder advancement) {
		return null;
	}

	/**
	 * @param recipeOutput The recipe output
	 * @param recipeId The recipe id. The directory is prepended
	 */
	public final void save(final RecipeOutput recipeOutput, final ResourceLocation recipeId) {
		ensureValid(recipeId);
		final var advancement = makeAdvancement(recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(AdvancementRequirements.Strategy.OR));
		final var realRecipeId = directory.isEmpty() ? recipeId : recipeId.withPrefix(directory + "/");
		recipeOutput.accept(realRecipeId, recipe(), advancement == null ? null : advancement.build(realRecipeId.withPrefix("recipes/")));
	}

	/**
	 * @param recipeOutput The recipe output
	 *
	 * @implNote This uses the recipes default recipe id, some builders might not be able to generate a good default id so this can potentially throw
	 */
	public final void save(final RecipeOutput recipeOutput) {
		save(recipeOutput, getDefaultRecipeId());
	}

	/**
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