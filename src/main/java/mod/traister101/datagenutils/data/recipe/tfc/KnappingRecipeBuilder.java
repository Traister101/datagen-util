package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.recipe.SimpleRecipeBuilder;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import org.jetbrains.annotations.Contract;
import java.util.*;

/**
 * A recipe builder for TFC's {@link KnappingRecipe}
 */
public final class KnappingRecipeBuilder extends SimpleRecipeBuilder {

	/**
	 * The rock knapping type
	 */
	public static final ResourceLocation ROCK = Helpers.identifier("rock");
	/**
	 * The clay knapping type
	 */
	public static final ResourceLocation CLAY = Helpers.identifier("clay");
	/**
	 * The fire clay knapping type
	 */
	public static final ResourceLocation FIRE_CLAY = Helpers.identifier("fire_clay");
	/**
	 * The leather knapping type
	 */
	public static final ResourceLocation LEATHER = Helpers.identifier("leather");
	/**
	 * The goat horn knapping type
	 */
	public static final ResourceLocation GOAT_HORN = Helpers.identifier("goat_horn");
	/**
	 * The default knapping recipe directory
	 */
	public static final String DEFAULT_DIRECTORY = "knapping";

	private final ItemStack result;
	private final DataManager.Reference<KnappingType> knappingType;
	private final List<String> pattern = new ArrayList<>();
	private boolean defaultOn = true;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Ingredient> ingredient = Optional.empty();

	/**
	 * The constructor
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param type The knapping type
	 * @param result The result stack
	 */
	public KnappingRecipeBuilder(final String directory, final ResourceLocation type, final ItemStack result) {
		super(directory);
		this.knappingType = KnappingType.MANAGER.getReference(type);
		this.result = result;
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #ROCK} knapping type
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder rock(final String directory, final ItemStack result) {
		return knapping(directory, ROCK, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #ROCK} knapping type and the default directory
	 * {@value DEFAULT_DIRECTORY}
	 *
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_ -> new")
	public static KnappingRecipeBuilder rock(final ItemStack result) {
		return knapping(ROCK, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #CLAY} knapping type
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder clay(final String directory, final ItemStack result) {
		return knapping(directory, CLAY, result);
	}


	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #CLAY} knapping type and the default directory
	 * {@value DEFAULT_DIRECTORY}
	 *
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_ -> new")
	public static KnappingRecipeBuilder clay(final ItemStack result) {
		return knapping(CLAY, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #FIRE_CLAY} knapping type
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder fireClay(final String directory, final ItemStack result) {
		return knapping(directory, FIRE_CLAY, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #FIRE_CLAY} knapping type and the default directory
	 * {@value DEFAULT_DIRECTORY}
	 *
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_ -> new")
	public static KnappingRecipeBuilder fireClay(final ItemStack result) {
		return knapping(FIRE_CLAY, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #LEATHER} knapping type
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder leather(final String directory, final ItemStack result) {
		return knapping(directory, LEATHER, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #LEATHER} knapping type and the default directory
	 * {@value DEFAULT_DIRECTORY}
	 *
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_ -> new")
	public static KnappingRecipeBuilder leather(final ItemStack result) {
		return knapping(LEATHER, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #GOAT_HORN} knapping type
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder goat(final String directory, final ItemStack result) {
		return knapping(directory, GOAT_HORN, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the {@link #GOAT_HORN} knapping type and the default directory
	 * {@value DEFAULT_DIRECTORY}
	 *
	 * @param result The result stack
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_ -> new")
	public static KnappingRecipeBuilder goat(final ItemStack result) {
		return knapping(GOAT_HORN, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s with the default directory {@value DEFAULT_DIRECTORY}
	 *
	 * @param type The kanpping type
	 * @param result The result
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _ -> new")
	public static KnappingRecipeBuilder knapping(final ResourceLocation type, final ItemStack result) {
		return knapping(DEFAULT_DIRECTORY, type, result);
	}

	/**
	 * A factory function for {@link KnappingRecipeBuilder}s
	 *
	 * @param directory The directory, can be empty to ignore
	 * @param type The kanpping type
	 * @param result The result
	 *
	 * @return A new {@link KnappingRecipeBuilder}
	 */
	@Contract("_, _, _ -> new")
	public static KnappingRecipeBuilder knapping(final String directory, final ResourceLocation type, final ItemStack result) {
		return new KnappingRecipeBuilder(directory, type, result);
	}

	@Override
	protected ResourceLocation getDefaultRecipeId() {
		return SimpleRecipeBuilder.getDefaultRecipeId(result.getItem());
	}

	@Override
	protected void ensureValid(final ResourceLocation recipeId) {
	}

	@Override
	protected Recipe<?> recipe() {
		return new KnappingRecipe(knappingType, KnappingPattern.from(defaultOn, pattern.toArray(String[]::new)), ingredient, result);
	}

	/**
	 * Sets non encoded squares to be enabled. Most TFC knapping recipes use this, though clay knapping is a good example of it not being used
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@Contract(value = " -> this", mutates = "this")
	public KnappingRecipeBuilder defaultOn() {
		defaultOn = true;
		return this;
	}

	/**
	 * Adds multiple rows to the pattern for this recipe.
	 *
	 * @param rows The recipe pattern as a list of rows
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@Contract(value = "_ -> this", mutates = "this")
	public KnappingRecipeBuilder pattern(final String... rows) {
		if (rows.length > KnappingPattern.MAX_HEIGHT) {
			throw new IllegalArgumentException("Too many rows:" + rows.length + " Max is " + KnappingPattern.MAX_HEIGHT);
		}
		Arrays.stream(rows).forEach(this::row);
		return this;
	}

	/**
	 * Adds a new row to the pattern for this recipe.
	 *
	 * @param row A single row for the recipe pattern
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@Contract(value = "_ -> this", mutates = "this")
	public KnappingRecipeBuilder row(final String row) {
		if (row.length() > KnappingPattern.MAX_WIDTH) {
			throw new IllegalArgumentException("Row:" + row.length() + " is too long. Max is " + KnappingPattern.MAX_WIDTH);
		}
		if (!pattern.isEmpty() && pattern.getFirst().length() != row.length()) {
			throw new IllegalArgumentException("Rows must be the same width. " + pattern.getFirst().length() + " is not " + row.length());
		}
		pattern.add(row);
		return this;
	}

	/**
	 * Adds a more strict matcher for what should be considered a valid ingredient
	 *
	 * @param ingredient The ingredient to more specifically match the held knapping item
	 *
	 * @return {@code this}
	 */
	@CanIgnoreReturnValue
	@Contract(value = "_ -> this", mutates = "this")
	public KnappingRecipeBuilder ingredient(final Ingredient ingredient) {
		this.ingredient = Optional.of(ingredient);
		return this;
	}
}