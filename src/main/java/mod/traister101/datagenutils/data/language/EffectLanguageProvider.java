package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

public abstract class EffectLanguageProvider extends RegistryLanguageSubProvider<MobEffect> {

	public EffectLanguageProvider(final DeferredRegister<MobEffect> register) {
		super(register, MobEffect::getDescriptionId);
	}
}