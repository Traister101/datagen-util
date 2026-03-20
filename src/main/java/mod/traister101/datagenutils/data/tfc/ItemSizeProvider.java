package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.common.component.size.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Contract;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for TFC's {@link ItemSizeDefinition}
 */
public abstract class ItemSizeProvider extends DataManagerProvider<ItemSizeDefinition> {

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param modid The modid
	 * @param registries The registries
	 */
	protected ItemSizeProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> registries) {
		super(ItemSizeManager.MANAGER, output, modid, registries);
	}

	/**
	 * {@return a new ItemSizeDefinition}
	 *
	 * @param item The item the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	@Contract("_, _, _ -> new")
	protected static ItemSizeDefinition size(final ItemLike item, final Size size, final Weight weight) {
		return size(Ingredient.of(item), size, weight);
	}

	/**
	 * {@return a new ItemSizeDefinition}
	 *
	 * @param tag A tag for what items the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	@Contract("_, _, _ -> new")
	protected static ItemSizeDefinition size(final TagKey<Item> tag, final Size size, final Weight weight) {
		return size(Ingredient.of(tag), size, weight);
	}

	/**
	 * {@return a new ItemSizeDefinition}
	 *
	 * @param ingredient An ingredient for what items the {@link ItemSizeDefinition} applies to
	 * @param size The size, determines what containers it can fit in
	 * @param weight The weight, determines the stack size (usually)
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	protected static ItemSizeDefinition size(final Ingredient ingredient, final Size size, final Weight weight) {
		return new ItemSizeDefinition(ingredient, size, weight);
	}
}