package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.common.conditions.ICondition;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;

/**
 * An advancement output styled after vanillas {@link net.minecraft.data.recipes.RecipeOutput}
 */
public interface AdvancementOutput {

	AdvancementHolder accept(ResourceLocation id, Advancement advancement, ICondition... conditions);

	void lang(LanguageTranslation languageTranslation);

	default AdvancementOutput withConditions(final ICondition... conditions) {
		return new ConditionalAdvancementOutput(this, conditions);
	}
}