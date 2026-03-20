package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * A language provider for {@link FluidType}s
 */
public abstract class FluidLanguageSubProvider extends RegistryLanguageSubProvider<FluidType> {

	/**
	 * The constructor
	 *
	 * @param register The fluid type register
	 */
	public FluidLanguageSubProvider(final DeferredRegister<FluidType> register) {
		super(register, FluidType::getDescriptionId);
	}
}