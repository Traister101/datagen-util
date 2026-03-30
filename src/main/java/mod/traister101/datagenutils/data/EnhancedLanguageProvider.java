package mod.traister101.datagenutils.data;

import com.google.gson.JsonObject;
import mod.traister101.datagenutils.data.language.*;
import mod.traister101.datagenutils.data.util.*;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.*;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.*;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.*;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Contract;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Smarter {@link LanguageProvider} that checks to make sure registered objects have lang
 * <pre>{@code
 *  // In datagen entry point
 *  EnhancedAdvancementProvider myModAdvancements = generator.addProvider(event.includeServer(), MyModAdvancements.create(packOutput, lookupProvider, existingFileHelper));
 *
 *  EnhancedLanguageProvider myModLanguage = generator.addProvider(event.includeClient(), MyModLanguage.create(packOutput, lookupProvider));
 *  // Add the advancement provider as an extra language provider
 *  myModLanguage.extraLanguage(myModAdvancements);
 *
 *  // Example impl of this class
 *  public final class MyModLanguage extends EnhancedLanguageProvider {
 *      public MyModLanguage(PackOutput output, CompletableFuture<Provider> registries) {
 *          super(output, registries, MODID, "en_us", List.of(<sub providers>));
 *      }
 *
 *      @Override
 *      protected void addTranslations() {
 *      // Translations unhandled by sub providers.
 *      // If using sub providers these should be plain translations.
 *      add("example.key.thing", "Example Thing");
 *      add(LanguageTranslation.of("example.key.other_thing", "Other Example Thing"));
 *      }
 *  }
 * }</pre>
 */
public abstract class EnhancedLanguageProvider implements DataProvider {

	/**
	 * The mod id
	 */
	protected final String modid;
	private final Set<LanguageTranslation> data = new TreeSet<>(Comparator.comparing(LanguageTranslation::key));
	private final PackOutput output;
	private final String locale;
	private final CompletableFuture<Provider> registries;
	private final List<EnhancedLanguageSubProvider> subProviders;
	private final List<ExtraLanguageProvider> extraLanguageProviders = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param output The pack output
	 * @param registries The registries
	 * @param modid The mod id
	 * @param locale The locale such as 'en_us'
	 * @param subProviders All the sub providers. Many useful sub providers exist in {@link mod.traister101.datagenutils.data.language}
	 */
	protected EnhancedLanguageProvider(final PackOutput output, final CompletableFuture<Provider> registries, final String modid, final String locale,
			final List<EnhancedLanguageSubProvider> subProviders) {
		this.output = output;
		this.registries = registries;
		this.modid = modid;
		this.locale = locale;
		this.subProviders = subProviders;
	}

	/**
	 * A helper function for {@link DeferredRegister}s which consist exclusively of registry names valid for
	 * {@link LanguageOutput#simple(Supplier)}
	 *
	 * @param register The register
	 * @param keyFunction A function to convert the registry object into a language key
	 * @param <T> The game object type
	 *
	 * @return A language sub provider for "simply named" objects sourced from a deferred register
	 *
	 * @throws IllegalArgumentException If the provided register contains any non "simply named" objects
	 */
	@Experimental
	@Contract(value = "_, _ -> new", pure = true)
	protected static <T> EnhancedLanguageSubProvider simpleStaticLanguage(final DeferredRegister<T> register, final Function<T, String> keyFunction) {
		{
			final var invalidHolders = register.getEntries().stream().map(Holder::getRegisteredName).filter(s -> s.contains("/")).toList();
			if (!invalidHolders.isEmpty()) {
				throw new IllegalArgumentException(
						"Unsupported registry name(s) in DeferredRegister[" + register.getRegistryName() + "]. Unsupported names: " + String.join(
								", ", invalidHolders));
			}
		}
		return new RegistryLanguageSubProvider<>(register, keyFunction) {
			@Override
			protected void addTranslations(final LanguageOutput<T> output) {
				register.getEntries().forEach(output::simple);
			}
		};
	}

	/**
	 * Adds extra {@link ExtraLanguageProvider}s to the language provider
	 *
	 * @param extraLanguageProviders One or many extra language providers.
	 *
	 * @return {@code this}
	 *
	 * @see EnhancedAdvancementProvider
	 * @see mod.traister101.datagenutils.data.tags.EnhancedTagsProvider EnhancedTagsProvider
	 */
	@Contract("_ -> this")
	@SuppressWarnings("unused")
	public EnhancedLanguageProvider extraLanguage(final ExtraLanguageProvider... extraLanguageProviders) {
		this.extraLanguageProviders.addAll(Arrays.asList(extraLanguageProviders));
		return this;
	}

	/**
	 * A helper function for dynamic registries which consist exclusively of registry names valid for {@link LanguageOutput#simple(Object)}
	 *
	 * @param modId The mod id
	 * @param registryKey The registry key
	 * @param keyFunction The function to convert an id to a language key
	 * @param keyStream A stream of resource keys which are "simply named"
	 * @param <T> The object type
	 *
	 * @return A language sub provider for "simply named" objects sourced from a dynamic registry
	 */
	@Experimental
	@Contract(value = "_, _, _, _ -> new", pure = true)
	protected final <T> EnhancedLanguageSubProvider simpleDynamicLanguage(final String modId, final ResourceKey<Registry<T>> registryKey,
			final Function<ResourceLocation, String> keyFunction, final Stream<ResourceKey<T>> keyStream) {
		return new DynamicRegistryLanguageSubProvider<>(registryKey, keyFunction, modId) {
			@Override
			protected void addTranslations(final LanguageOutput<ResourceKey<T>> output) {
				keyStream.forEach(output::simple);
			}
		};
	}

	/**
	 * Add all translations (not already handled via {@link ExtraLanguageProvider})
	 * If you do not want to use {@link EnhancedLanguageSubProvider}s override {@link #knownObjects(Provider)}
	 */
	protected abstract void addTranslations();

	@Override
	public final CompletableFuture<?> run(final CachedOutput cache) {
		return registries.thenCompose(provider -> {
			Stream.concat(extraLanguageProviders.stream().flatMap(ExtraLanguageProvider::extraTranslations),
					subProviders.stream().flatMap(EnhancedLanguageSubProvider::translations)).forEach(this::add);
			addTranslations();
			Stream.concat(knownObjects(provider),
							subProviders.stream().<KnownObjects<?>>map(subProvider -> subProvider.knownObjects(provider)).filter(Objects::nonNull))
					.forEach(this::validate);

			if (!data.isEmpty()) {
				final var path = output.getOutputFolder(Target.RESOURCE_PACK).resolve(modid).resolve("lang").resolve(locale + ".json");
				return CompletableFuture.allOf(save(cache, path));
			}

			return CompletableFuture.allOf();
		});
	}

	@Override
	public String getName() {
		return "Languages: " + locale + " for mod: " + modid;
	}

	/**
	 * Returns a stream of the known objects commonly Items, Blocks and Entities though essentially any type can be checked
	 *
	 * @param provider The registry provider
	 *
	 * @return A stream of known registry contents
	 */
	protected Stream<KnownObjects<?>> knownObjects(@SuppressWarnings("unused") final Provider provider) {
		return Stream.empty();
	}

	/**
	 * Add a language translation
	 *
	 * @param languageTranslation The language translation
	 */
	protected final void add(final LanguageTranslation languageTranslation) {
		if (!data.add(languageTranslation)) throw new IllegalArgumentException("Duplicate Language Translation" + languageTranslation);
	}

	/**
	 * Adds a language translation
	 *
	 * @param key A translation key
	 * @param translation The translation
	 *
	 * @implNote Constructs a {@link LanguageTranslation} and delegates to {@link #add(LanguageTranslation)}
	 */
	protected final void add(final String key, final String translation) {
		add(LanguageTranslation.of(key, translation));
	}

	private <T> void validate(final KnownObjects<T> contents) {
		contents.knownObjects()
				.filter(knownObject -> !data.contains(LanguageTranslation.of(knownObject.langKey(), "untranslated")))
				.forEach(knownObject -> {
					throw new IllegalStateException(
							String.format(Locale.ROOT, "Missing lang entry for '%s' in '%s'", knownObject.name(), contents.name()));
				});
	}

	private CompletableFuture<?> save(final CachedOutput cache, final Path target) {
		final var json = new JsonObject();
		data.forEach(translation -> json.addProperty(translation.key(), translation.translation()));

		return DataProvider.saveStable(cache, json, target);
	}
}