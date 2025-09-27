package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.common.conditions.ICondition;

import net.minecraft.advancements.AdvancementHolder;

/**
 * An advancement output styled after vanillas {@link net.minecraft.data.recipes.RecipeOutput}
 */
public interface AdvancementOutput {

	/**
	 * Accept an advancement, id and optionally {@link ICondition}s
	 *
	 * @param advancement The advancement
	 * @param conditions The conditions
	 *
	 * @return An advancement holder
	 */
	AdvancementHolder accept(AdvancementHolder advancement, ICondition... conditions);

	/**
	 * Adds extra language translations, useful for simplifying advancement translations
	 *
	 * @param languageTranslation A language translation
	 */
	void lang(LanguageTranslation languageTranslation);

	/**
	 * Helper (and primary means) of adding conditions
	 *
	 * @param conditions A var arg of conditions
	 *
	 * @return A {@link AdvancementOutput} which automatically applies the given conditions
	 */
	default AdvancementOutput withConditions(final ICondition... conditions) {
		return new ConditionalAdvancementOutput(this, conditions);
	}
}