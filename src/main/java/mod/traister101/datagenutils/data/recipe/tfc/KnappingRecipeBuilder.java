package mod.traister101.datagenutils.data.recipe.tfc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.recipe.SimpleRecipeBuilder;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

public final class KnappingRecipeBuilder extends SimpleRecipeBuilder {

	public static final ResourceLocation ROCK = Helpers.identifier("rock");
	public static final ResourceLocation CLAY = Helpers.identifier("clay");
	public static final ResourceLocation FIRE_CLAY = Helpers.identifier("fire_clay");
	public static final ResourceLocation LEATHER = Helpers.identifier("leather");
	public static final ResourceLocation GOAT_HORN = Helpers.identifier("goat_horn");

	private final ItemStack result;
	private final DataManager.Reference<KnappingType> knappingType;
	private final List<String> pattern = new ArrayList<>();
	private boolean defaultOn = true;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Ingredient> ingredient = Optional.empty();

	public KnappingRecipeBuilder(final String directory, final ResourceLocation type, final ItemStack result) {
		super(directory);
		this.knappingType = KnappingType.MANAGER.getCheckedReference(type);
		this.result = result;
	}

	public static KnappingRecipeBuilder rock(final String directory, final ItemStack result) {
		return knapping(directory, ROCK, result);
	}

	public static KnappingRecipeBuilder rock(final ItemStack result) {
		return knapping(ROCK, result);
	}

	public static KnappingRecipeBuilder clay(final String directory, final ItemStack result) {
		return knapping(directory, CLAY, result);
	}

	public static KnappingRecipeBuilder clay(final ItemStack result) {
		return knapping(CLAY, result);
	}

	public static KnappingRecipeBuilder fireClay(final String directory, final ItemStack result) {
		return knapping(directory, FIRE_CLAY, result);
	}

	public static KnappingRecipeBuilder fireClay(final ItemStack result) {
		return knapping(FIRE_CLAY, result);
	}

	public static KnappingRecipeBuilder leather(final String directory, final ItemStack result) {
		return knapping(directory, LEATHER, result);
	}

	public static KnappingRecipeBuilder leather(final ItemStack result) {
		return knapping(LEATHER, result);
	}

	public static KnappingRecipeBuilder goat(final String directory, final ItemStack result) {
		return knapping(directory, GOAT_HORN, result);
	}

	public static KnappingRecipeBuilder goat(final ItemStack result) {
		return knapping(GOAT_HORN, result);
	}

	public static KnappingRecipeBuilder knapping(final ResourceLocation type, final ItemStack result) {
		return knapping("knapping", type, result);
	}

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

	@CanIgnoreReturnValue
	public KnappingRecipeBuilder defaultOff() {
		defaultOn = false;
		return this;
	}

	@CanIgnoreReturnValue
	public KnappingRecipeBuilder defaultOn() {
		defaultOn = true;
		return this;
	}

	@CanIgnoreReturnValue
	public KnappingRecipeBuilder pattern(final String... rows) {
		if (rows.length == KnappingPattern.MAX_HEIGHT) {
			throw new IllegalArgumentException("Too many rows:" + rows.length + " Max is " + KnappingPattern.MAX_HEIGHT);
		}
		Arrays.stream(rows).forEach(this::pattern);
		return this;
	}

	@CanIgnoreReturnValue
	public KnappingRecipeBuilder pattern(final String row) {
		if (row.length() == KnappingPattern.MAX_WIDTH) {
			throw new IllegalArgumentException("Row:" + row.length() + " is too long. Max is " + KnappingPattern.MAX_WIDTH);
		}
		if (!pattern.isEmpty() && pattern.getFirst().length() != row.length()) {
			throw new IllegalArgumentException("Rows must be the same width. " + pattern.getFirst().length() + " is not " + row.length());
		}
		pattern.add(row);
		return this;
	}

	@CanIgnoreReturnValue
	public KnappingRecipeBuilder ingredient(final Ingredient ingredient) {
		this.ingredient = Optional.of(ingredient);
		return this;
	}
}