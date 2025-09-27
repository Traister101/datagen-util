package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.advancements.AdvancementHolder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class ConditionalAdvancementOutput implements AdvancementOutput {

	private final AdvancementOutput inner;
	private final ICondition[] conditions;

	@Override
	public AdvancementHolder accept(final AdvancementHolder advancement, final ICondition... conditions) {
		final ICondition[] innerConditions;
		if (conditions.length == 0) {
			innerConditions = this.conditions;
		} else if (this.conditions.length == 0) {
			innerConditions = conditions;
		} else {
			innerConditions = ArrayUtils.addAll(this.conditions, conditions);
		}
		return inner.accept(advancement, innerConditions);
	}

	@Override
	public void lang(final LanguageTranslation languageTranslation) {
		inner.lang(languageTranslation);
	}
}