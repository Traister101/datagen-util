package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

/**
 * A language provider for {@link Item}s
 */
public abstract class ItemLanguageProvider extends RegistryLanguageSubProvider<Item> {

	/**
	 * The constructor
	 *
	 * @param register The item register
	 */
	public ItemLanguageProvider(final DeferredRegister<Item> register) {
		super(register, Item::getDescriptionId);
	}
}