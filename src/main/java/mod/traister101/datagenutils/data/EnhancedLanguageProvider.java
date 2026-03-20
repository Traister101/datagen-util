package mod.traister101.datagenutils.data;

import com.google.gson.JsonObject;
import mod.traister101.datagenutils.data.language.*;
import mod.traister101.datagenutils.data.util.*;
import net.neoforged.neoforge.common.data.LanguageProvider;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.*;
import net.minecraft.data.PackOutput.Target;

import org.jetbrains.annotations.Contract;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
	 * Returns a stream of the known objects commonly Items, Blocks and Entities though especially any type can be checked
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
							String.format(Locale.ROOT, "Missing lang entry for '%s' in '%s'", knownObject.id(), contents.name()));
				});
	}

	private CompletableFuture<?> save(final CachedOutput cache, final Path target) {
		final var json = new JsonObject();
		data.forEach(translation -> json.addProperty(translation.key(), translation.translation()));

		return DataProvider.saveStable(cache, json, target);
	}
}