package mod.traister101.datagenutils.data.tfc;

import mod.traister101.datagenutils.data.util.tfc.TFCFluidHeat;
import net.dries007.tfc.util.data.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public abstract class FluidHeatProvider extends DataManagerProvider<FluidHeat> {

	protected FluidHeatProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> lookup) {
		super(FluidHeat.MANAGER, output, modid, lookup);
	}

	protected static FluidHeat fluidHeat(final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		return new FluidHeat(fluid, meltTemperature, TFCFluidHeat.HEAT_CAPACITY / baseHeatCapacity);
	}

	protected final void add(final String name, final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		add(name, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}

	protected final void add(final ResourceLocation name, final Fluid fluid, final float baseHeatCapacity, final float meltTemperature) {
		add(name, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}

	protected final void add(final DataManager.Reference<FluidHeat> reference, final Fluid fluid, final float baseHeatCapacity,
			final float meltTemperature) {
		add(reference, fluidHeat(fluid, baseHeatCapacity, meltTemperature));
	}
}