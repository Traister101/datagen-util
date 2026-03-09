package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

public abstract class FluidLanguageSubProvider extends RegistryLanguageSubProvider<FluidType> {

	public FluidLanguageSubProvider(final DeferredRegister<FluidType> register) {
		super(register, FluidType::getDescriptionId);
	}
}