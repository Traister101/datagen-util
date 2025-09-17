package mod.traister101.datagenutils.data.tfc;

import mod.traister101.datagenutils.data.EnhancedRecipeProvider.AdditionalRecipeProvider;
import net.dries007.tfc.common.component.heat.*;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.*;
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
	 * @param fluidHeat The fluid heat to use
	 * @param units The units
	 */
	protected static HeatDefinition heat(final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		return heat(ingredient, (fluidHeat.specificHeatCapacity() / FluidHeatProvider.HEAT_CAPACITY) * (units / 100f),
				fluidHeat.meltTemperature() * 0.6f, fluidHeat.meltTemperature() * 0.8f);
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

	/**
	 * @param location The fluid heat location
	 */
	protected static FluidHeat fluidHeat(final ResourceLocation location) {
		return FluidHeat.MANAGER.getOrThrow(location);
	}

	@Override
	public Stream<? extends RecipeHolder<?>> additionalRecipes() {
		return meltingRecipes.stream();
	}

	/**
	 * @param name The name
	 * @param ingredient The ingredient
	 * @param metal The TFC metal
	 * @param units The units
	 */
	protected void addAndMelt(final String name, final Ingredient ingredient, final Metal metal, final int units) {
		addAndMelt(ResourceLocation.fromNamespaceAndPath(modid, name), ingredient, metal, units);
	}

	/**
	 * @param id The id
	 * @param ingredient The ingredient
	 * @param metal The TFC metal
	 * @param units The units
	 */
	private void addAndMelt(final ResourceLocation id, final Ingredient ingredient, final Metal metal, final int units) {
		final var fluidHeat = fluidHeat(Helpers.identifier(metal.getSerializedName()));
		addMelt(id.withPrefix("heating/"), ingredient, fluidHeat, units);
		add(id, heat(ingredient, fluidHeat, units));
	}

	/**
	 * @param id The id
	 * @param ingredient The ingredient
	 * @param fluidHeat The fluid heat
	 * @param units The units
	 */
	private void addMelt(final ResourceLocation id, final Ingredient ingredient, final FluidHeat fluidHeat, final int units) {
		meltingRecipes.add(new RecipeHolder<>(id,
				new HeatingRecipe(ingredient, ItemStackProvider.empty(), new FluidStack(fluidHeat.fluid(), units), fluidHeat.meltTemperature(),
						false)));
	}
}