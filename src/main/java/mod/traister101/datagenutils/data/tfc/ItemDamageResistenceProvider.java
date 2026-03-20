package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.data.ItemDamageResistance;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Contract;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for TFC's {@link ItemDamageResistance}
 */
public abstract class ItemDamageResistenceProvider extends DataManagerProvider<ItemDamageResistance> {

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param modid The modid
	 * @param registries The registries
	 */
	protected ItemDamageResistenceProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> registries) {
		super(ItemDamageResistance.MANAGER, output, modid, registries);
	}

	/**
	 * {@return a new ItemDamageResistance}
	 *
	 * @param item The item
	 * @param piercing The piercing resistance
	 * @param slashing The slashing resistance
	 * @param crushing The crushing resistance
	 */
	@SuppressWarnings("unused")
	@Contract("_, _, _, _ -> new")
	protected static ItemDamageResistance damageResistance(final ItemLike item, final int piercing, final int slashing, final int crushing) {
		return damageResistance(Ingredient.of(item), piercing, slashing, crushing);
	}

	/**
	 * {@return a new ItemDamageResistance}
	 *
	 * @param tag The tag
	 * @param piercing The piercing resistance
	 * @param slashing The slashing resistance
	 * @param crushing The crushing resistance
	 */
	@SuppressWarnings("unused")
	@Contract("_, _, _, _ -> new")
	protected static ItemDamageResistance damageResistance(final TagKey<Item> tag, final int piercing, final int slashing, final int crushing) {
		return damageResistance(Ingredient.of(tag), piercing, slashing, crushing);
	}

	/**
	 * {@return a new ItemDamageResistance}
	 *
	 * @param ingredient The ingredient
	 * @param piercing The piercing resistance
	 * @param slashing The slashing resistance
	 * @param crushing The crushing resistance
	 */
	@SuppressWarnings("unused")
	@Contract("_, _, _, _ -> new")
	protected static ItemDamageResistance damageResistance(final Ingredient ingredient, final int piercing, final int slashing, final int crushing) {
		return new ItemDamageResistance(ingredient, new PhysicalDamage(piercing, slashing, crushing));
	}
}