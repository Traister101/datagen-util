package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public abstract class EnhancedEntityTagsProvider extends StaticRegistryObjectTagsProvider<EntityType<?>> {

	@SuppressWarnings("deprecation")
	protected EnhancedEntityTagsProvider(final PackOutput output, final CompletableFuture<Provider> registries, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.ENTITY_TYPE, registries, entityType -> entityType.builtInRegistryHolder().key(), modId, existingFileHelper);
	}
}