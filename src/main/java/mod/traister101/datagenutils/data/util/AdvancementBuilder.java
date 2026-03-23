package mod.traister101.datagenutils.data.util;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.*;
import com.google.errorprone.annotations.CheckReturnValue;

import net.minecraft.advancements.*;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.*;
import java.util.*;
import java.util.function.*;

/**
 * An advancement builder
 */
@CanIgnoreReturnValue
public final class AdvancementBuilder {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static final Optional<ResourceLocation> ROOT_RECIPE_ADVANCEMENT = Optional.of(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private final Optional<ResourceLocation> parent;
	private final ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
	private final boolean sendsTelemetryEvent;
	private final List<ChildAdvancement> children = new ArrayList<>();
	@Nullable
	private SimpleDisplayInfo display;
	private AdvancementRewards rewards = AdvancementRewards.EMPTY;
	@Nullable
	private AdvancementRequirements requirements;
	private AdvancementRequirements.Strategy strategy = AdvancementRequirements.Strategy.AND;

	private AdvancementBuilder(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<ResourceLocation> parent,
			final boolean sendsTelemetryEvent) {
		this.parent = parent;
		this.sendsTelemetryEvent = sendsTelemetryEvent;
	}

	/**
	 * A helper to create an advancement builder for a recipe
	 *
	 * @param recipeId The recipe id
	 *
	 * @return The advancement builder
	 */
	@CheckReturnValue
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
	@CheckReturnValue
	@Contract(" -> new")
	@SuppressWarnings("unused")
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
	@CheckReturnValue
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static AdvancementBuilder childOf(final AdvancementHolder parent) {
		return childOf(parent.id());
	}

	/**
	 * Creates an advancement builder that's a child of another advancement
	 *
	 * @param parentId The parent advancement id
	 *
	 * @return An advancement builder
	 */
	@CheckReturnValue
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static AdvancementBuilder childOf(final ResourceLocation parentId) {
		return new AdvancementBuilder(Optional.of(parentId), true);
	}

	/**
	 * Creates an advancement builder that's a child of another advancement
	 *
	 * @param parent The parent advancement
	 *
	 * @return An advancement builder
	 *
	 * @deprecated Poorly named. Use {@link #childOf(AdvancementHolder)} instead
	 */
	@CheckReturnValue
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	@InlineMe(replacement = "AdvancementBuilder.childOf(parent)", imports = {"mod.traister101.datagenutils.data.util.AdvancementBuilder"})
	@Deprecated(since = "1.2.3", forRemoval = true)
	public static AdvancementBuilder child(final AdvancementHolder parent) {
		return childOf(parent);
	}

	/**
	 * Creates an advancement builder that's a child of another advancement
	 *
	 * @param parentId The parent advancement
	 *
	 * @return An advancement builder
	 *
	 * @deprecated Poorly named. Use {@link #childOf(ResourceLocation)} instead
	 */
	@CheckReturnValue
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	@InlineMe(replacement = "AdvancementBuilder.childOf(parentId)", imports = {"mod.traister101.datagenutils.data.util.AdvancementBuilder"})
	@Deprecated(since = "1.2.3", forRemoval = true)
	public static AdvancementBuilder child(final ResourceLocation parentId) {
		return childOf(parentId);
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
	 * @see #requireAny()
	 * @see AdvancementRequirements.Strategy#AND
	 * @see AdvancementRequirements.Strategy#OR
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder requirementsStrategy(final AdvancementRequirements.Strategy strategy) {
		this.strategy = strategy;
		return this;
	}

	/**
	 * Sets the requirements strategy to {@link Strategy#OR}
	 *
	 * @return {@code this}
	 */
	@SuppressWarnings("unused")
	@Contract(value = "-> this", mutates = "this")
	public AdvancementBuilder requireAny() {
		return requirementsStrategy(Strategy.OR);
	}

	/**
	 * Helper for adding a {@link InventoryChangeTrigger.TriggerInstance} {@link Criterion}
	 *
	 * @param name The criteria name
	 * @param items The items
	 *
	 * @return {@code this}
	 */
	@SuppressWarnings("unused")
	@Contract(value = "_, _ -> this", mutates = "this")
	public AdvancementBuilder hasItems(final String name, final ItemLike... items) {
		return hasItems(name, Builder.item().of(items));
	}

	/**
	 * Helper for adding a {@link InventoryChangeTrigger.TriggerInstance} {@link Criterion}
	 *
	 * @param name The criteria name
	 * @param items The predicate builders
	 *
	 * @return {@code this}
	 */
	@Contract(value = "_, _ -> this", mutates = "this")
	public AdvancementBuilder hasItems(final String name, final ItemPredicate.Builder... items) {
		return hasItems(name, Arrays.stream(items).map(Builder::build).toArray(ItemPredicate[]::new));
	}

	/**
	 * Helper for adding a {@link InventoryChangeTrigger.TriggerInstance} {@link Criterion}
	 *
	 * @param name The criteria name
	 * @param items The item predicates
	 *
	 * @return {@code this}
	 */
	@Contract(value = "_, _ -> this", mutates = "this")
	public AdvancementBuilder hasItems(final String name, final ItemPredicate... items) {
		return addCriterion(name, InventoryChangeTrigger.TriggerInstance.hasItems(items));
	}

	/**
	 * This is rarely useful, you almost always just want to use {@link #addCriterion(String, Criterion)}
	 *
	 * @param requirements The advancement requirements
	 *
	 * @return The builder
	 */
	@SuppressWarnings("unused")
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
	 * @return {@code this}
	 */
	@SuppressWarnings("unused")
	@Contract(value = "_ -> this", mutates = "this")
	public AdvancementBuilder display(final UnaryOperator<SimpleDisplayInfo.SimpleDisplayInfoBuilder> displayInfoBuilder) {
		return display(displayInfoBuilder.apply(SimpleDisplayInfo.builder()));
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
	@SuppressWarnings("unused")
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
	 * Adds a child advancement
	 *
	 * @param id A string for the id, if no namespace is specified it'll default to {@value ResourceLocation#DEFAULT_NAMESPACE} as per
	 * {@link ResourceLocation#parse(String)}
	 * @param child The child advancement
	 *
	 * @return {@code this}
	 */
	@SuppressWarnings("unused")
	@Contract(value = "_, _ -> this", mutates = "this")
	public AdvancementBuilder child(final String id, final Consumer<AdvancementBuilder> child) {
		return child(ResourceLocation.parse(id), child);
	}

	/**
	 * Adds a child advancement
	 *
	 * @param id The child id
	 * @param child The child advancement
	 *
	 * @return {@code this}
	 */
	@Contract("_, _ -> this")
	public AdvancementBuilder child(final ResourceLocation id, final Consumer<AdvancementBuilder> child) {
		children.add(new ChildAdvancement(id, child));
		return this;
	}

	/**
	 * Builds the children advancements. You should only ever call this if you want to use this advancement builder in a vanilla provider. If you are
	 * using {@link mod.traister101.datagenutils.data.EnhancedAdvancementProvider EnhancedAdvancementProvider} and
	 * {@link mod.traister101.datagenutils.data.AdvancementSubProvider AdvancementSubProvider} always use one of the {@code save} overloads
	 *
	 * @param parent The parent advancement holder
	 *
	 * @return A list of all the advancement children
	 *
	 * @see #save(AdvancementOutput, String)
	 * @see #save(AdvancementOutput, ResourceLocation)
	 */
	@Unmodifiable
	@SuppressWarnings("unused")
	@Contract(value = "_ -> new", pure = true)
	public List<AdvancementHolder> buildChildren(final AdvancementHolder parent) {
		return children.stream().map(child -> {
			final var builder = childOf(parent);
			child.consumer.accept(builder);
			return builder.build(child.id);
		}).toList();
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
	 * Saves the advancement and any children to the output
	 *
	 * @param output The advancement output
	 * @param advancementId The advancement id
	 *
	 * @return The saved advancement
	 */
	@CheckReturnValue
	public AdvancementHolder save(final AdvancementOutput output, final ResourceLocation advancementId) {
		if (display != null) {
			display.save(output, advancementId);
		}
		final var advancement = build(advancementId);

		children.forEach(c -> {
			final var child = childOf(advancement);
			c.consumer.accept(child);
			child.save(output, c.id);
		});

		return output.accept(advancement);
	}

	/**
	 * Saves the advancement to the output
	 *
	 * @param output The advancement output
	 * @param id A string for the id, if no namespace is specified it'll default to {@value ResourceLocation#DEFAULT_NAMESPACE} as per
	 * {@link ResourceLocation#parse(String)}
	 *
	 * @return The saved advancement
	 */
	@CheckReturnValue
	@SuppressWarnings("unused")
	public AdvancementHolder save(final AdvancementOutput output, final String id) {
		return save(output, ResourceLocation.parse(id));
	}

	private record ChildAdvancement(ResourceLocation id, Consumer<AdvancementBuilder> consumer) {}
}