package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.entity.EntityType;

/**
 * A language provider for {@link EntityType}s
 */
public abstract class EntityLanguageProvider extends RegistryLanguageSubProvider<EntityType<?>> {

	/**
	 * The constructor
	 *
	 * @param register The entity register
	 */
	public EntityLanguageProvider(final DeferredRegister<EntityType<?>> register) {
		super(register, EntityType::getDescriptionId);
	}
}