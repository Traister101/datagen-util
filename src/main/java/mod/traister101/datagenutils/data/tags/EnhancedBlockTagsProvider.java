package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for block tags allowing definition of tag lang with the rest of the tag definitions
 */
public abstract class EnhancedBlockTagsProvider extends StaticRegistryObjectTagsProvider<Block> {

	/**
	 * The constructor
	 *
	 * @param output The pack output
	 * @param registries The registries
	 * @param modId The mod id
	 * @param existingFileHelper The existing files
	 */
	@SuppressWarnings("deprecation")
	public EnhancedBlockTagsProvider(final PackOutput output, final CompletableFuture<Provider> registries, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BLOCK, registries, block -> block.builtInRegistryHolder().key(), modId, existingFileHelper);
	}
}
