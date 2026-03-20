package mod.traister101.datagenutils.data.tfc;

import mod.traister101.datagenutils.data.util.tfc.TFCFluidHeat;
import net.dries007.tfc.util.data.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import org.jetbrains.annotations.Contract;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for TFC's {@link FluidHeat}
 */
public abstract class FluidHeatProvider extends DataManagerProvider<FluidHeat> {

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param modid The modid
	 * @param registries The registries
	 */
	protected FluidHeatProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> registries) {
		super(FluidHeat.MANAGER, output, modid, registries);
	}

	/**
	 * {@return a new FluidHeat}
	 *
	 * @param fluid The fluid
	 * @param baseHeatCapacity The base heat capacity of the fluid
	 * @param meltTemperature The melting temperature of the fluid
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	protected static FluidHeat fluidHeat(final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		return new FluidHeat(fluid, meltTemperature, TFCFluidHeat.HEAT_CAPACITY / baseHeatCapacity);
	}

	/**
	 * Add a {@link FluidHeat}
	 *
	 * @param name The name
	 * @param fluid The fluid
	 * @param baseHeatCapacity The base heat capacity of the fluid
	 * @param meltTemperature The melting temperature of the fluid
	 */
	protected final void add(final String name, final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		add(name, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}

	/**
	 * Add a {@link FluidHeat}
	 *
	 * @param name The name
	 * @param fluid The fluid
	 * @param baseHeatCapacity The base heat capacity of the fluid
	 * @param meltTemperature The melting temperature of the fluid
	 */
	protected final void add(final ResourceLocation name, final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		add(name, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}

	/**
	 * Add a {@link FluidHeat}
	 *
	 * @param reference The reference
	 * @param fluid The fluid
	 * @param baseHeatCapacity The base heat capacity of the fluid
	 * @param meltTemperature The melting temperature of the fluid
	 */
	protected final void add(final DataManager.Reference<FluidHeat> reference, final Fluid fluid, final float baseHeatCapacity,
			final float meltTemperature) {
		add(reference, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}
}