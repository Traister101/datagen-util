package mod.traister101.datagenutils.data.tags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.*;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A specialization for game objects that are statically registered such as {@link Item}s or {@link Block}s
 *
 * @param <T> The game object type
 */
public abstract class StaticRegistryObjectTagsProvider<T> extends EnhancedTagsProvider<T> {

	private final Function<T, ResourceKey<T>> keyExtractor;

	/**
	 * The full constructor
	 *
	 * @param output The pack output
	 * @param registryKey The registry this provider creates tags for
	 * @param registries The registries
	 * @param parentTags The parent tags, used
	 * @param keyExtractor A function that returns a resource key representing the object
	 * @param modId The mod id
	 * @param existingFileHelper The existing file helper
	 */
	protected StaticRegistryObjectTagsProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey,
			final CompletableFuture<Provider> registries, final CompletableFuture<TagsProvider.TagLookup<T>> parentTags,
			final Function<T, ResourceKey<T>> keyExtractor, final String modId, final @Nullable ExistingFileHelper existingFileHelper) {
		super(output, registryKey, registries, parentTags, modId, existingFileHelper);
		this.keyExtractor = keyExtractor;
	}

	/**
	 * Shorthand constructor without parent tags
	 *
	 * @param output The pack output
	 * @param registryKey The registry this provider creates tags for
	 * @param registries The registries
	 * @param keyExtractor A function that returns a resource key representing the object
	 * @param modId The mod id
	 * @param existingFileHelper The existing file helper
	 */
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

	/**
	 * A tag appender that handles static registry objects like {@link Item} or {@link Block}
	 */
	protected class IntrinsicTagAppender extends TagAppender<IntrinsicTagAppender> {

		/**
		 * The constructor
		 *
		 * @param builder The tag builder
		 * @param tag The tag
		 */
		protected IntrinsicTagAppender(final TagBuilder builder, final TagKey<T> tag) {
			super(builder, tag);
		}

		/**
		 * Adds multiple registered game object entries to the tag
		 *
		 * @param entries The entries
		 *
		 * @return {@code this}
		 */
		@SafeVarargs
		@Contract("_ -> this")
		@SuppressWarnings({"unused", "varargs"})
		public final IntrinsicTagAppender add(final T... entries) {
			Stream.of(entries).forEach(this::add);
			return this;
		}

		/**
		 * Add an optional tag entry using a registered game object
		 *
		 * @param entry The game object
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public IntrinsicTagAppender addOptional(final T entry) {
			return addOptional(keyExtractor.apply(entry));
		}

		/**
		 * Adds a registered game object entry to the tag
		 *
		 * @param entry The game object entry
		 *
		 * @return {@code this}
		 */
		public IntrinsicTagAppender add(final T entry) {
			return add(keyExtractor.apply(entry));
		}

		/**
		 * Removes a registered game objet entry from the tag
		 *
		 * @param entry The registered game object entry
		 *
		 * @return {@code this}
		 */
		public IntrinsicTagAppender remove(final T entry) {
			return remove(keyExtractor.apply(entry));
		}

		/**
		 * Removes multiple registered game object entries from the tag
		 *
		 * @param first The first
		 * @param entries The rest
		 *
		 * @return {@code this}
		 */
		@SafeVarargs
		@Contract("_, _ -> this")
		@SuppressWarnings({"unused", "varargs"})
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