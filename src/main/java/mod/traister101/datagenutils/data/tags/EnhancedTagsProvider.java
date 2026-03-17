package mod.traister101.datagenutils.data.tags;

import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.language.ExtraLanguageProvider;
import mod.traister101.datagenutils.data.util.LanguageTranslation;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;

import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.*;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.*;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.*;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.*;

/**
 * An enhanced {@link TagsProvider} allowing definition of tag lang with the rest of the tag definitions
 *
 * @param <T> The type this provider creates tags for
 */
public abstract class EnhancedTagsProvider<T> implements DataProvider, ExtraLanguageProvider {

	protected final PackOutput.PathProvider pathProvider;
	protected final ResourceKey<? extends Registry<T>> registryKey;
	protected final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();
	protected final String modId;
	@Nullable
	protected final ExistingFileHelper existingFileHelper;
	private final CompletableFuture<Provider> lookupProvider;
	private final CompletableFuture<Void> contentsDone = new CompletableFuture<>();
	private final CompletableFuture<TagLookup<T>> parentProvider;
	private final ExistingFileHelper.IResourceType resourceType;
	private final ExistingFileHelper.IResourceType elementResourceType; // FORGE: Resource type for validating required references to datapack registry elements.
	private final Map<TagKey<T>, String> languageTranslations = new HashMap<>();

	protected EnhancedTagsProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey,
			final CompletableFuture<HolderLookup.Provider> registries, final String modId, final @Nullable ExistingFileHelper existingFileHelper) {
		this(output, registryKey, registries, CompletableFuture.completedFuture(TagLookup.empty()), modId, existingFileHelper);
	}

	protected EnhancedTagsProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey,
			final CompletableFuture<HolderLookup.Provider> registries, final CompletableFuture<TagLookup<T>> parentTags, final String modId,
			final @Nullable ExistingFileHelper existingFileHelper) {
		this.pathProvider = output.createRegistryTagsPathProvider(registryKey);
		this.registryKey = registryKey;
		this.parentProvider = parentTags;
		this.lookupProvider = registries;
		this.modId = modId;
		this.existingFileHelper = existingFileHelper;
		this.resourceType = new ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
		this.elementResourceType = new ResourceType(PackType.SERVER_DATA, ".json", CommonHooks.prefixNamespace(registryKey.location()));
	}

	// Forge: Allow customizing the path for a given tag or returning null
	@Nullable
	protected Path getPath(ResourceLocation id) {
		return pathProvider.json(id);
	}

	protected abstract void addTags(HolderLookup.Provider registries);

	@Override
	public CompletableFuture<?> run(final CachedOutput output) {
		record CombinedData<T>(HolderLookup.Provider contents, TagLookup<T> parent) {}

		return createContentsProvider().thenApply(registries -> {
			contentsDone.complete(null);
			return registries;
		}).thenCombineAsync(parentProvider, CombinedData::new, Util.backgroundExecutor()).thenCompose(data -> {
			final var registries = data.contents.lookupOrThrow(registryKey);
			Predicate<ResourceLocation> entryPredicate = resourceLocation -> registries.get(ResourceKey.create(registryKey, resourceLocation))
					.isPresent();
			Predicate<ResourceLocation> tagPredicate = resourceLocation -> builders.containsKey(resourceLocation) || data.parent.contains(
					TagKey.create(registryKey, resourceLocation));
			return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
				final var tagId = entry.getKey();
				final var builder = entry.getValue();
				final var tagEntries = builder.build();
				{
					final var missingEntries = tagEntries.stream()
							.filter((tagEntry) -> !tagEntry.verifyIfPresent(entryPredicate, tagPredicate))
							.filter(this::missing)
							.toList();
					if (!missingEntries.isEmpty()) {
						throw new IllegalArgumentException(
								String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", tagId,
										missingEntries.stream().map(Objects::toString).collect(Collectors.joining(","))));
					}
				}

				final var path = getPath(tagId);
				// Neo: Allow running this data provider without writing it. Recipe provider needs valid tags.
				if (path == null) return CompletableFuture.completedFuture(null);
				final var removed = builder.getRemoveEntries().toList();
				return DataProvider.saveStable(output, data.contents, TagFile.CODEC, new TagFile(tagEntries, builder.isReplace(), removed), path);
			}).toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public String getName() {
		return "Tags for " + registryKey.location() + " mod id " + modId;
	}

	private boolean missing(final TagEntry reference) {
		// Optional tags should not be validated
		if (reference.isRequired()) {
			return existingFileHelper == null || !existingFileHelper.exists(reference.getId(),
					reference.isTag() ? resourceType : elementResourceType);
		}
		return false;
	}

	protected TagAppender<?> tag(final TagKey<T> tag) {
		class TagAppenderImpl extends TagAppender<TagAppenderImpl> {

			protected TagAppenderImpl(final TagBuilder builder, final TagKey<T> tag) {
				super(builder, tag);
			}

			@Override
			protected TagAppenderImpl self() {
				return this;
			}
		}
		return new TagAppenderImpl(getOrCreateRawBuilder(tag), tag);
	}

	@Override
	public Stream<LanguageTranslation> extraTranslations() {
		return languageTranslations.entrySet().stream().map(entry -> LanguageTranslation.tag(entry.getKey(), entry.getValue()));
	}

	protected TagBuilder getOrCreateRawBuilder(final TagKey<T> tag) {
		if (existingFileHelper != null) {
			existingFileHelper.trackGenerated(tag.location(), resourceType);
		}
		return this.builders.computeIfAbsent(tag.location(), tagId -> TagBuilder.create());
	}

	@SuppressWarnings("unused")
	public CompletableFuture<TagLookup<T>> contentsGetter() {
		return this.contentsDone.thenApply(unused -> tagKey -> Optional.ofNullable(builders.get(tagKey.location())));
	}

	protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
		return this.lookupProvider.thenApply(registries -> {
			this.builders.clear();
			this.addTags(registries);
			return registries;
		});
	}

	@FunctionalInterface
	public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {

		static <T> TagLookup<T> empty() {
			return tag -> Optional.empty();
		}

		default boolean contains(final TagKey<T> tag) {
			return this.apply(tag).isPresent();
		}
	}

	@CanIgnoreReturnValue
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	protected abstract class TagAppender<A extends TagAppender<A>> {

		private final TagBuilder builder;
		private final TagKey<T> tag;

		/**
		 * You generally shouldn't override tags in a mod context
		 * Shorthand for {@code replace(true)}
		 */
		public A replace() {
			return replace(true);
		}

		/**
		 * You generally shouldn't override tags in a mod context
		 *
		 * @param replace If this tag should replace
		 */
		public A replace(final boolean replace) {
			builder.replace(replace);
			return self();
		}

		/**
		 * Creates a translation for this tag via {@link LanguageTranslation#langify(String)}
		 */
		@SuppressWarnings("unused")
		public A simpleName() {
			return name(LanguageTranslation.langify(tag.location().getPath()));
		}

		/**
		 * Creates a translation for this tag
		 *
		 * @param name The name of this tag
		 */
		public A name(final String name) {
			languageTranslations.put(tag, name);
			return self();
		}

		/**
		 * Adds an optional element, this should be the id of the object you want in the tag
		 *
		 * @param elementId The id of the element you want to optionally add
		 */
		public A addOptionalElement(final ResourceLocation elementId) {
			return add(TagEntry.optionalElement(elementId));
		}

		/**
		 * Adds optional tags
		 *
		 * @param tags The tags to add
		 */
		@SafeVarargs
		@SuppressWarnings({"unused", "varargs"})
		public final A addOptional(final TagKey<T>... tags) {
			Arrays.stream(tags).forEach(this::addOptional);
			return self();
		}

		/**
		 * Add an optional tag
		 *
		 * @param tag The tag to optionally add
		 */
		public A addOptional(final TagKey<T> tag) {
			return addOptionalTag(tag.location());
		}

		/**
		 * Add an optional tag from a raw {@link ResourceLocation}
		 *
		 * @param tagId The tag id
		 */
		public A addOptionalTag(final ResourceLocation tagId) {
			return add(TagEntry.optionalTag(tagId));
		}

		public A addOptional(final ResourceKey<T> resourceKey) {
			return addOptionalElement(resourceKey.location());
		}

		/**
		 * @see #addOptional(ResourceKey)
		 */
		public A add(final ResourceKey<T> resourceKey) {
			return add(TagEntry.element(resourceKey.location()));
		}

		/**
		 * @see #addOptional(TagKey)
		 */
		public A add(final TagKey<T> tag) {
			return add(TagEntry.tag(tag.location()));
		}

		public A add(final TagEntry tagEntry) {
			builder.add(tagEntry);
			return self();
		}

		@SafeVarargs
		@SuppressWarnings({"unused", "varargs"})
		public final A add(final TagKey<T>... tags) {
			Arrays.stream(tags).forEach(this::add);
			return self();
		}

		@SafeVarargs
		@SuppressWarnings({"unused", "varargs"})
		public final A add(final ResourceKey<T>... resourceKeys) {
			Arrays.stream(resourceKeys).forEach(this::add);
			return self();
		}

		@SafeVarargs
		@SuppressWarnings({"unused", "varargs"})
		public final A remove(final ResourceKey<T> firstResourceKey, final ResourceKey<T>... resourceKeys) {
			remove(firstResourceKey);
			Arrays.stream(resourceKeys).forEach(this::remove);
			return self();
		}

		public A remove(final ResourceKey<T> resourceKey) {
			remove(TagEntry.element(resourceKey.location()));
			return self();
		}

		@SafeVarargs
		@SuppressWarnings({"unused", "varargs"})
		public final A remove(final TagKey<T> firstTag, final TagKey<T>... tags) {
			remove(firstTag);
			Arrays.stream(tags).forEach(this::remove);
			return self();
		}

		/**
		 * @param tag The tag to remove
		 */
		public A remove(final TagKey<T> tag) {
			remove(TagEntry.tag(tag.location()));
			return self();
		}

		/**
		 * @param entry The entry to remove
		 */
		public A remove(final TagEntry entry) {
			builder.remove(entry);
			return self();
		}

		protected abstract A self();
	}
}