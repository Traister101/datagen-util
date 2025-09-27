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
 * {@link Exclusions}
 *
 * @param <B> The parent builder type
 */
@ToString
@RequiredArgsConstructor
public final class ShapedPatternBuilder<B> implements ShapedRecipeBuilder<B> {

	/**
	 * The parent builder
	 */
	@ToString.Exclude
	private final B parentBuilder;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

	@Override
	public B define(final Character symbol, final TagKey<Item> tag) {
		return define(symbol, Ingredient.of(tag));
	}

	@Override
	public B define(final Character symbol, final ItemLike item) {
		return define(symbol, Ingredient.of(item));
	}

	@Override
	public B define(final Character symbol, final Ingredient ingredient) {
		if (key.containsKey(symbol)) throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");

		if (symbol == ' ') throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");

		key.put(symbol, ingredient);
		return parentBuilder;
	}

	@Override
	public B pattern(final String... pattern) {
		Arrays.stream(pattern).forEach(this::pattern);
		return parentBuilder;
	}

	@Override
	public B pattern(final String row) {
		if (!rows.isEmpty() && row.length() != rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		}

		rows.add(row);
		return parentBuilder;
	}

	/**
	 * Ensure the pattern is valid
	 *
	 * @param recipeId The recipe id (for more useful error reporting)
	 */
	public void validate(final ResourceLocation recipeId) {
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
	 * Builds the {@link ShapedRecipePattern}
	 *
	 * @return The {@link ShapedRecipePattern} for the current builder state
	 */
	public ShapedRecipePattern build() {
		return ShapedRecipePattern.of(key, rows);
	}

	/**
	 * Helper interface for Lombok {@link lombok.experimental.Delegate} code gen
	 */
	@SuppressWarnings("unused")
	public interface Exclusions {

		void validate(final ResourceLocation recipeId);

		ShapedRecipePattern build();
	}
}