package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for fluid tags allowing definition of tag lang with the rest of the tag definitions
 */
public abstract class EnhancedFluidTagsProvider extends StaticRegistryObjectTagsProvider<Fluid> {

	/**
	 * The constructor
	 *
	 * @param output The pack output
	 * @param registries The registries
	 * @param modId The mod id
	 * @param existingFileHelper The existing files
	 */
	@SuppressWarnings("deprecation")
	public EnhancedFluidTagsProvider(final PackOutput output, final CompletableFuture<Provider> registries, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.FLUID, registries, fluid -> fluid.builtInRegistryHolder().key(), modId, existingFileHelper);
	}
}