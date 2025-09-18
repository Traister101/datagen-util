package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.common.component.food.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public abstract class FoodProvider extends DataManagerProvider<FoodDefinition> {

	protected FoodProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> lookup) {
		super(FoodCapability.MANAGER, output, modid, lookup);
	}

	/**
	 * @param item The item
	 * @param foodData The food data
	 */
	protected static FoodDefinition edible(final ItemLike item, final FoodData foodData) {
		return edible(Ingredient.of(item), foodData);
	}

	/**
	 * @param tag The tag for this food type
	 * @param foodData The food data
	 */
	protected static FoodDefinition edible(final TagKey<Item> tag, final FoodData foodData) {
		return edible(Ingredient.of(tag), foodData);
	}

	/**
	 * @param ingredient The ingredient for this food definition
	 * @param foodData The food data
	 */
	protected static FoodDefinition edible(final Ingredient ingredient, final FoodData foodData) {
		return new FoodDefinition(ingredient, foodData, true);
	}

	/**
	 * @param item The item
	 * @param foodData The food data
	 */
	protected static FoodDefinition nonEdible(final ItemLike item, final FoodData foodData) {
		return nonEdible(Ingredient.of(item), foodData);
	}

	/**
	 * @param tag The tag for this food type
	 * @param foodData The food data
	 */
	protected static FoodDefinition nonEdible(final TagKey<Item> tag, final FoodData foodData) {
		return nonEdible(Ingredient.of(tag), foodData);
	}

	/**
	 * @param ingredient The ingredient for this food definition
	 * @param foodData The food data
	 */
	protected static FoodDefinition nonEdible(final Ingredient ingredient, final FoodData foodData) {
		return new FoodDefinition(ingredient, foodData, false);
	}

	/**
	 * @param item The item that this definition is for
	 * @param foodData The food data
	 */
	protected void addEdible(final ItemLike item, final FoodData foodData) {
		add(BuiltInRegistries.ITEM.getKey(item.asItem()), edible(item, foodData));
	}

	/**
	 * @param item The item that this definition is for
	 * @param foodData The food data
	 */
	protected void addNonEdible(final ItemLike item, final FoodData foodData) {
		add(BuiltInRegistries.ITEM.getKey(item.asItem()), nonEdible(item, foodData));
	}
}