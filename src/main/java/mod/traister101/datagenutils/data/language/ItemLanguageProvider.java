package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

public abstract class ItemLanguageProvider extends RegistryLanguageSubProvider<Item> {

	public ItemLanguageProvider(final DeferredRegister<Item> register) {
		super(register, Item::getDescriptionId);
	}
}