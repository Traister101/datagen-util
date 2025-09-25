package mod.traister101.datagenutils.data.util;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.*;
import com.google.errorprone.annotations.CheckReturnValue;

import net.minecraft.advancements.*;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.*;
import java.util.Map;

/**
 * @param <B> The parent builder type
 */
@CanIgnoreReturnValue
@RequiredArgsConstructor
class CommonAdvancementBuilder<B> {

	private final B parent;
	private final ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
	private AdvancementRewards rewards = AdvancementRewards.EMPTY;
	@Nullable
	private AdvancementRequirements requirements;
	private AdvancementRequirements.Strategy strategy = AdvancementRequirements.Strategy.AND;

	/**
	 * @param rewardsBuilder The advancement rewards builder
	 */
	public B rewards(final AdvancementRewards.Builder rewardsBuilder) {
		return rewards(rewardsBuilder.build());
	}

	/**
	 * @param rewards The advancement rewards
	 */
	public B rewards(final AdvancementRewards rewards) {
		this.rewards = rewards;
		return parent;
	}

	/**
	 * @param name The criteria name
	 * @param criterion The criterion
	 */
	public B addCriterion(final String name, final Criterion<?> criterion) {
		criteria.put(name, criterion);
		return parent;
	}

	/**
	 * @param strategy The strategy to use for the automatic {@link AdvancementRequirements}.
	 *
	 * @see AdvancementRequirements.Strategy#AND
	 * @see AdvancementRequirements.Strategy#OR
	 */
	public B requirementsStrategy(final AdvancementRequirements.Strategy strategy) {
		this.strategy = strategy;
		return parent;
	}

	/**
	 * This is rarely useful, you almost always just want to use {@link #addCriterion(String, Criterion)}
	 *
	 * @param requirements The advancement requirements
	 */
	public B requirements(final AdvancementRequirements requirements) {
		this.requirements = requirements;
		return parent;
	}

	@CheckReturnValue
	public CommonAdvancementInfo build() {
		final var criteria = this.criteria.buildOrThrow();
		return new CommonAdvancementInfo(criteria, requirements == null ? strategy.create(criteria.keySet()) : requirements, rewards);
	}

	public interface Excludes {

		CommonAdvancementInfo build();
	}

	@Value
	@Accessors(fluent = true)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CommonAdvancementInfo {

		/**
		 * The criteria
		 */
		@Unmodifiable
		Map<String, Criterion<?>> criteria;
		/**
		 * The requirements
		 */
		AdvancementRequirements requirements;
		/**
		 * The rewards
		 */
		AdvancementRewards rewards;
	}
}