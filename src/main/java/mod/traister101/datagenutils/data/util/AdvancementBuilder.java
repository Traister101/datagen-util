package mod.traister101.datagenutils.data.util;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.*;
import com.google.errorprone.annotations.CheckReturnValue;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import lombok.*;
import org.jetbrains.annotations.*;
import java.util.Optional;

@CanIgnoreReturnValue
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdvancementBuilder {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static final Optional<ResourceLocation> ROOT_RECIPE_ADVANCEMENT = Optional.of(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private final Optional<ResourceLocation> parent;
	private final ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
	private final boolean sendsTelemetryEvent;
	@Nullable
	private SimpleDisplayInfo display;
	private AdvancementRewards rewards = AdvancementRewards.EMPTY;
	@Nullable
	private AdvancementRequirements requirements;
	private AdvancementRequirements.Strategy strategy = AdvancementRequirements.Strategy.AND;

	/**
	 * A helper to create an advancement builder for a recipe
	 *
	 * @param recipeId The recipe id
	 *
	 * @return The advancement builder
	 */
	@Contract("_ -> new")
	public static AdvancementBuilder recipe(final ResourceLocation recipeId) {
		return new AdvancementBuilder(ROOT_RECIPE_ADVANCEMENT, false).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirementsStrategy(AdvancementRequirements.Strategy.OR);
	}

	/**
	 * Create an advancement builder for a root advancement
	 *
	 * @return An advancement builder
	 */
	@Contract(" -> new")
	public static AdvancementBuilder root() {
		return new AdvancementBuilder(Optional.empty(), true);
	}

	/**
	 * Creates an advancement builder that's a child of another advancement
	 *
	 * @param parent The parent advancement
	 *
	 * @return An advancement builder
	 */
	@Contract("_ -> new")
	public static AdvancementBuilder child(final AdvancementHolder parent) {
		return new AdvancementBuilder(Optional.of(parent.id()), true);
	}

	/**
	 * Creates an advancement builder that's a child of another advancement
	 *
	 * @param parentId The parent advancement
	 *
	 * @return An advancement builder
	 */
	@Contract("_ -> new")
	public static AdvancementBuilder child(final ResourceLocation parentId) {
		return new AdvancementBuilder(Optional.of(parentId), true);
	}

	/**
	 * Sets the rewards
	 *
	 * @param rewardsBuilder The advancement rewards builder
	 *
	 * @return The builder
	 */
	@Contract("_ -> this")
	public AdvancementBuilder rewards(final AdvancementRewards.Builder rewardsBuilder) {
		return rewards(rewardsBuilder.build());
	}

	/**
	 * Sets the rewards
	 *
	 * @param rewards The advancement rewards
	 *
	 * @return The builder
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder rewards(final AdvancementRewards rewards) {
		this.rewards = rewards;
		return this;
	}

	/**
	 * Adds an unlock condition
	 *
	 * @param name The criteria name
	 * @param criterion The criterion
	 *
	 * @return The builder
	 */
	@Contract(value = "_, _ -> this", mutates = "this")
	public AdvancementBuilder addCriterion(final String name, final Criterion<?> criterion) {
		criteria.put(name, criterion);
		return this;
	}

	/**
	 * Sets the requirement strategy
	 *
	 * @param strategy The strategy to use for the automatic {@link AdvancementRequirements}.
	 *
	 * @return The builder
	 *
	 * @see AdvancementRequirements.Strategy#AND
	 * @see AdvancementRequirements.Strategy#OR
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder requirementsStrategy(final AdvancementRequirements.Strategy strategy) {
		this.strategy = strategy;
		return this;
	}

	/**
	 * This is rarely useful, you almost always just want to use {@link #addCriterion(String, Criterion)}
	 *
	 * @param requirements The advancement requirements
	 *
	 * @return The builder
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder requirements(final AdvancementRequirements requirements) {
		this.requirements = requirements;
		return this;
	}

	/**
	 * Sets the display properties
	 *
	 * @param displayInfoBuilder The display info builder
	 *
	 * @return The builder
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder display(final SimpleDisplayInfo.SimpleDisplayInfoBuilder displayInfoBuilder) {
		return display(displayInfoBuilder.build());
	}

	/**
	 * Sets the display properties
	 *
	 * @param icon The icon stack
	 * @param title The title
	 * @param description The description
	 * @param background The background texture, optional
	 * @param type The advancement type
	 * @param showToast If the advancement should show a toast when unlocked
	 * @param announceChat If a chat message should be sent when the advancement is unlocked
	 * @param hidden If this is a hidden advancement
	 *
	 * @return The builder
	 */
	@Contract(value = "_, _, _, _, _, _, _, _ -> this", mutates = "this")
	public AdvancementBuilder display(final ItemStack icon, final String title, final String description, final @Nullable ResourceLocation background,
			final AdvancementType type, final boolean showToast, final boolean announceChat, final boolean hidden) {
		return display(new SimpleDisplayInfo(icon, title, description, background, type, showToast, announceChat, hidden));
	}

	/**
	 * Sets the display properties
	 *
	 * @param displayInfo The display info
	 *
	 * @return The builder
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder display(final SimpleDisplayInfo displayInfo) {
		this.display = displayInfo;
		return this;
	}

	/**
	 * Build the advancement
	 *
	 * @param advancementId The advancement id
	 *
	 * @return The built advancement
	 */
	@CheckReturnValue
	@Contract("_ -> new")
	public AdvancementHolder build(final ResourceLocation advancementId) {
		final var criteria = this.criteria.buildOrThrow();
		final var requirements = this.requirements == null ? strategy.create(criteria.keySet()) : this.requirements;
		final DisplayInfo displayInfo;
		if (display != null) {
			displayInfo = display.toInfo(advancementId);
		} else {
			displayInfo = null;
		}
		return new AdvancementHolder(advancementId,
				new Advancement(parent, Optional.ofNullable(displayInfo), rewards, criteria, requirements, sendsTelemetryEvent));
	}

	/**
	 * Saves the advancement to the output
	 *
	 * @param output The advancement output
	 * @param advancementId The advancement id
	 *
	 * @return The saved advancement
	 */
	public AdvancementHolder save(final AdvancementOutput output, final ResourceLocation advancementId) {
		if (display != null) {
			display.save(output, advancementId);
		}
		final var build = build(advancementId);

		return output.accept(build);
	}

	/**
	 * Saves the advancement to the output
	 *
	 * @param output The advancement output
	 * @param id A resource string for the location, if no namespace is specified it'll default to {@value ResourceLocation#DEFAULT_NAMESPACE}
	 *
	 * @return The saved advancement
	 */
	public AdvancementHolder save(final AdvancementOutput output, final String id) {
		return save(output, ResourceLocation.parse(id));
	}
}