package mod.traister101.datagenutils.data.tfc;

import mod.traister101.datagenutils.data.EnhancedRecipeProvider.AdditionalRecipeProvider;
import mod.traister101.datagenutils.data.util.tfc.TFCFluidHeat;
import net.dries007.tfc.common.component.heat.*;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.data.FluidHeat;
import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class ItemHeatProvider extends DataManagerProvider<HeatDefinition> implements AdditionalRecipeProvider {

	private final List<RecipeHolder<HeatingRecipe>> meltingRecipes = new ArrayList<>();

	protected ItemHeatProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> lookup) {
		super(HeatCapability.MANAGER, output, modid, lookup);
	}

	/**
	 * @param ingredient The ingredient
	 * @param fluidHeat The fluid heat to use see {@link TFCFluidHeat}
	 * @param units The units
	 */
	protected static HeatDefinition heat(final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		return heat(ingredient, (fluidHeat.specificHeatCapacity() / TFCFluidHeat.HEAT_CAPACITY) * (units / 100F),
				fluidHeat.meltTemperature() * 0.6F, fluidHeat.meltTemperature() * 0.8F);
	}

	/**
	 * @param ingredient The ingredient
	 * @param heatCapacity The heat capacity
	 * @param forgingTemperature The forging temperature
	 * @param weldingTemperature The welding temperature
	 */
	protected static HeatDefinition heat(final Ingredient ingredient, final float heatCapacity, final float forgingTemperature,
			final float weldingTemperature) {
		return new HeatDefinition(ingredient, heatCapacity, forgingTemperature, weldingTemperature);
	}

	@Override
	public Stream<? extends RecipeHolder<?>> additionalRecipes() {
		return meltingRecipes.stream();
	}

	/**
	 * @param name The name
	 * @param ingredient The ingredient
	 * @param fluidHeat The fluid heat see {@link TFCFluidHeat}
	 * @param units The units
	 */
	protected final void addAndMelt(final String name, final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		addAndMelt(ResourceLocation.fromNamespaceAndPath(modid, name), ingredient, fluidHeat, units);
	}

	/**
	 * @param id The id
	 * @param ingredient The ingredient
	 * @param fluidHeat The fluid heat see {@link TFCFluidHeat}
	 * @param units The units
	 */
	protected final void addAndMelt(final ResourceLocation id, final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		addMelt(id.withPrefix("heating/"), ingredient, fluidHeat, units);
		add(id, heat(ingredient, fluidHeat, units));
	}

	/**
	 * @param id The id
	 * @param ingredient The ingredient
	 * @param fluidHeat The fluid heat see {@link TFCFluidHeat}
	 * @param units The units
	 */
	protected final void addMelt(final ResourceLocation id, final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		meltingRecipes.add(new RecipeHolder<>(id,
				new HeatingRecipe(ingredient, ItemStackProvider.empty(), new FluidStack(fluidHeat.fluid(), units), fluidHeat.meltTemperature(),
						false)));
	}
}