package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class StaticRegistryObjectTagsProvider<T> extends EnhancedTagsProvider<T> {

	private final Function<T, ResourceKey<T>> keyExtractor;

	protected StaticRegistryObjectTagsProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey,
			final CompletableFuture<Provider> registries, final CompletableFuture<TagLookup<T>> parentTags,
			final Function<T, ResourceKey<T>> keyExtractor, final String modId, final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, registryKey, registries, parentTags, modId, existingFileHelper);
		this.keyExtractor = keyExtractor;
	}

	protected StaticRegistryObjectTagsProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey,
			final CompletableFuture<Provider> registries, final Function<T, ResourceKey<T>> keyExtractor, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, registryKey, registries, modId, existingFileHelper);
		this.keyExtractor = keyExtractor;
	}

	@Override
	protected IntrinsicTagAppender tag(final TagKey<T> tag) {
		return new IntrinsicTagAppender(getOrCreateRawBuilder(tag), tag);
	}

	protected class IntrinsicTagAppender extends TagAppender<IntrinsicTagAppender> {

		protected IntrinsicTagAppender(final TagBuilder builder, final TagKey<T> tag) {
			super(builder, tag);
		}

		@SafeVarargs
		public final IntrinsicTagAppender add(final T... entries) {
			Stream.of(entries).forEach(this::add);
			return this;
		}

		/**
		 * Add an optional tag entry using a registered game object
		 *
		 * @param entry The game object
		 */
		public IntrinsicTagAppender addOptional(final T entry) {
			return addOptional(keyExtractor.apply(entry));
		}

		public IntrinsicTagAppender add(final T entry) {
			return add(keyExtractor.apply(entry));
		}

		public IntrinsicTagAppender remove(final T entry) {
			return remove(keyExtractor.apply(entry));
		}

		@SafeVarargs
		public final IntrinsicTagAppender remove(final T first, final T... entries) {
			remove(first);
			Arrays.stream(entries).forEach(this::remove);
			return this;
		}

		@Override
		protected IntrinsicTagAppender self() {
			return this;
		}
	}
}