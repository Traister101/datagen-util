package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.entity.EntityType;

public abstract class EntityLanguageProvider extends RegistryLanguageSubProvider<EntityType<?>> {

	public EntityLanguageProvider(final DeferredRegister<EntityType<?>> register) {
		super(register, EntityType::getDescriptionId);
	}
}