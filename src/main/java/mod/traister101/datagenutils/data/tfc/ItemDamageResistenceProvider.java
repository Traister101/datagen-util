package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.data.ItemDamageResistance;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public abstract class ItemDamageResistenceProvider extends DataManagerProvider<ItemDamageResistance> {

	protected ItemDamageResistenceProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> lookup) {
		super(ItemDamageResistance.MANAGER, output, modid, lookup);
	}

	protected static ItemDamageResistance damageResistance(final ItemLike item, final int piercing, final int slashing, final int crushing) {
		return damageResistance(Ingredient.of(item), piercing, slashing, crushing);
	}

	protected static ItemDamageResistance damageResistance(final TagKey<Item> tag, final int piercing, final int slashing, final int crushing) {
		return damageResistance(Ingredient.of(tag), piercing, slashing, crushing);
	}

	protected static ItemDamageResistance damageResistance(final Ingredient ingredient, final int piercing, final int slashing, final int crushing) {
		return new ItemDamageResistance(ingredient, new PhysicalDamage(piercing, slashing, crushing));
	}
}