package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.common.component.size.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public abstract class ItemSizeProvider extends DataManagerProvider<ItemSizeDefinition> {

	protected ItemSizeProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> lookup) {
		super(ItemSizeManager.MANAGER, output, modid, lookup);
	}

	/**
	 * @param item The item the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	protected static ItemSizeDefinition size(final ItemLike item, final Size size, final Weight weight) {
		return size(Ingredient.of(item), size, weight);
	}

	/**
	 * @param tag A tag for what items the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	protected static ItemSizeDefinition size(final TagKey<Item> tag, final Size size, final Weight weight) {
		return size(Ingredient.of(tag), size, weight);
	}

	/**
	 * @param ingredient An ingredient for what items the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	protected static ItemSizeDefinition size(final Ingredient ingredient, final Size size, final Weight weight) {
		return new ItemSizeDefinition(ingredient, size, weight);
	}
}