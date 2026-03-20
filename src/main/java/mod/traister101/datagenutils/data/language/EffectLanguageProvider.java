package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

/**
 * A language provider for {@link MobEffect}s
 */
public abstract class EffectLanguageProvider extends RegistryLanguageSubProvider<MobEffect> {

	/**
	 * The constructor
	 *
	 * @param register The effect register
	 */
	public EffectLanguageProvider(final DeferredRegister<MobEffect> register) {
		super(register, MobEffect::getDescriptionId);
	}
}