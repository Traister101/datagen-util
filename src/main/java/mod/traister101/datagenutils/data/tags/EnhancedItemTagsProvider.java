package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class EnhancedItemTagsProvider extends StaticRegistryObjectTagsProvider<Item> {

	private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
	private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

	@SuppressWarnings("deprecation")
	public EnhancedItemTagsProvider(final PackOutput output, final CompletableFuture<Provider> registries,
			final CompletableFuture<TagLookup<Item>> parentTags, final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.ITEM, registries, parentTags, item -> item.builtInRegistryHolder().key(), modId, existingFileHelper);
		this.blockTags = blockTags;
	}

	@SuppressWarnings("deprecation")
	public EnhancedItemTagsProvider(final PackOutput output, final CompletableFuture<Provider> registries,
			final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.ITEM, registries, item -> item.builtInRegistryHolder().key(), modId, existingFileHelper);
		this.blockTags = blockTags;
	}

	/**
	 * Copies the contents of the given block tag to the given item tag
	 *
	 * @param blockTag The block tag to copy
	 * @param itemTag The item tag to copy to
	 */
	protected void copy(final TagKey<Block> blockTag, final TagKey<Item> itemTag) {
		tagsToCopy.put(blockTag, itemTag);
	}

	@Override
	protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
		return super.createContentsProvider().thenCombine(blockTags, (provider, blockTags) -> {
			tagsToCopy.forEach((blockTag, itemTag) -> blockTags.apply(blockTag)
					.orElseThrow(() -> new IllegalStateException("Missing block tag " + itemTag.location()))
					.build()
					.forEach(getOrCreateRawBuilder(itemTag)::add));
			return provider;
		});
	}
}