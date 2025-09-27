package mod.traister101.datagenutils.data.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * A shaped recipe builder
 *
 * @param <B> The parent builder type
 */
@SuppressWarnings("unused")
public interface ShapedRecipeBuilder<B> {

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param tag The tag
	 *
	 * @return The builder
	 */
	B define(Character symbol, TagKey<Item> tag);

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param item The item
	 *
	 * @return The builder
	 */
	B define(Character symbol, ItemLike item);

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param ingredient The ingredient value
	 *
	 * @return The builder
	 */
	B define(Character symbol, Ingredient ingredient);

	/**
	 * Adds multiple rows to the pattern for this recipe.
	 *
	 * @param pattern The recipe pattern as a list of rows
	 *
	 * @return The builder
	 */
	B pattern(String... pattern);

	/**
	 * Adds a new row to the pattern for this recipe.
	 *
	 * @param row A single row for the recipe pattern
	 *
	 * @return The builder
	 */
	@SuppressWarnings("UnusedReturnValue")
	B pattern(String row);
}