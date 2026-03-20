package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.common.component.food.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Contract;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for TFC's {@link FoodDefinition}
 */
public abstract class FoodProvider extends DataManagerProvider<FoodDefinition> {

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param modid The modid
	 * @param registries The registries
	 */
	protected FoodProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> registries) {
		super(FoodCapability.MANAGER, output, modid, registries);
	}

	/**
	 * {@return new FoodDefinition for a edible food}
	 *
	 * @param item The item
	 * @param foodData The food data
	 */
	@Contract("_, _ -> new")
	protected static FoodDefinition edible(final ItemLike item, final FoodData foodData) {
		return edible(Ingredient.of(item), foodData);
	}

	/**
	 * {@return new FoodDefinition for a edible food}
	 *
	 * @param tag The tag for this food type
	 * @param foodData The food data
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	protected static FoodDefinition edible(final TagKey<Item> tag, final FoodData foodData) {
		return edible(Ingredient.of(tag), foodData);
	}

	/**
	 * {@return new FoodDefinition for a edible food}
	 *
	 * @param ingredient The ingredient for this food definition
	 * @param foodData The food data
	 */
	@Contract(value = "_, _ -> new", pure = true)
	protected static FoodDefinition edible(final Ingredient ingredient, final FoodData foodData) {
		return new FoodDefinition(ingredient, foodData, true);
	}

	/**
	 * {@return new FoodDefinition for a non edible food}
	 *
	 * @param item The item
	 * @param foodData The food data
	 */
	@Contract("_, _ -> new")
	protected static FoodDefinition nonEdible(final ItemLike item, final FoodData foodData) {
		return nonEdible(Ingredient.of(item), foodData);
	}

	/**
	 * {@return new FoodDefinition for a non edible food}
	 *
	 * @param tag The tag for this food type
	 * @param foodData The food data
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	protected static FoodDefinition nonEdible(final TagKey<Item> tag, final FoodData foodData) {
		return nonEdible(Ingredient.of(tag), foodData);
	}

	/**
	 * {@return new FoodDefinition for a non edible food}
	 *
	 * @param ingredient The ingredient for this food definition
	 * @param foodData The food data
	 */
	@Contract(value = "_, _ -> new", pure = true)
	protected static FoodDefinition nonEdible(final Ingredient ingredient, final FoodData foodData) {
		return new FoodDefinition(ingredient, foodData, false);
	}

	/**
	 * Add an edible food
	 *
	 * @param item The item that this definition is for
	 * @param foodData The food data
	 */
	@SuppressWarnings("unused")
	protected void addEdible(final ItemLike item, final FoodData foodData) {
		add(BuiltInRegistries.ITEM.getKey(item.asItem()), edible(item, foodData));
	}

	/**
	 * Add a non-edible food
	 *
	 * @param item The item that this definition is for
	 * @param foodData The food data
	 */
	@SuppressWarnings("unused")
	protected void addNonEdible(final ItemLike item, final FoodData foodData) {
		add(BuiltInRegistries.ITEM.getKey(item.asItem()), nonEdible(item, foodData));
	}
}