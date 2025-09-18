package mod.traister101.datagenutils.data.util;

import com.google.common.collect.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import lombok.*;
import java.util.*;

/**
 * This class is intended to be used through composition via {@link lombok.experimental.Delegate}. See
 * {@link ShapedRecipePatternBuilder.DelegateExclusions}
 *
 * @param <B> The parent builder type
 */
@ToString
@RequiredArgsConstructor
@SuppressWarnings("unused")
public final class ShapedRecipePatternBuilder<B> {

	/**
	 * The parent builder
	 */
	@ToString.Exclude
	private final B parentBuilder;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param tag The tag
	 */
	public B define(final Character symbol, final TagKey<Item> tag) {
		return define(symbol, Ingredient.of(tag));
	}

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param item The item
	 */
	public B define(final Character symbol, final ItemLike item) {
		return define(symbol, Ingredient.of(item));
	}

	/**
	 * Defines a key for the recipe pattern.
	 *
	 * @param symbol The symbol key
	 * @param ingredient The ingredient value
	 */
	public B define(final Character symbol, final Ingredient ingredient) {
		if (key.containsKey(symbol)) throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");

		if (symbol == ' ') throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");

		key.put(symbol, ingredient);
		return parentBuilder;
	}

	/**
	 * Adds a new row to the pattern for this recipe.
	 *
	 * @param row A single row for the recipe pattern
	 */
	@SuppressWarnings("UnusedReturnValue")
	public B row(final String row) {
		if (!rows.isEmpty() && row.length() != rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		}

		rows.add(row);
		return parentBuilder;
	}

	/**
	 * Adds multiple rows to the pattern for this recipe.
	 *
	 * @param pattern The recipe pattern as a list of rows
	 */
	public B pattern(final String... pattern) {
		Arrays.stream(pattern).forEach(this::row);
		return parentBuilder;
	}

	/**
	 * Ensure the pattern is valid
	 *
	 * @param recipeId The recipe id (for more useful error reporting)
	 */
	public void ensureValid(final ResourceLocation recipeId) {
		if (rows.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
		}

		final var set = Sets.newHashSet(key.keySet());
		set.remove(' ');

		for (final var pattern : rows) {
			for (int i = 0; i < pattern.length(); ++i) {
				final var symbol = pattern.charAt(i);
				if (!key.containsKey(symbol) && symbol != ' ') {
					throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + symbol + "'");
				}

				set.remove(symbol);
			}
		}

		if (!set.isEmpty()) {
			throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
		}

		if (rows.size() == 1 && rows.getFirst().length() == 1) {
			throw new IllegalStateException("Shaped recipe " + recipeId + " only takes in a single item - it should be a shapeless recipe instead");
		}
	}

	/**
	 * @return The {@link ShapedRecipePattern} for the current builder state
	 */
	public ShapedRecipePattern build() {
		return ShapedRecipePattern.of(key, rows);
	}

	/**
	 * Helper interface for Lombok {@link lombok.experimental.Delegate} code gen
	 */
	public interface DelegateExclusions {

		void ensureValid(final ResourceLocation recipeId);

		ShapedRecipePattern build();
	}
}