package mod.traister101.datagenutils.data;

import mod.traister101.datagenutils.data.EnhancedLanguageProvider.ExtraLanguageProvider;
import mod.traister101.datagenutils.data.util.*;
import net.neoforged.neoforge.common.conditions.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper.ResourceType;

import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An enhanced Advancement Provider.
 * Use this provider like so
 * <pre>{@code
 *  public final class BuiltInAvdancements {
 *
 *      public static EnhancedAdvancementProvider create(final PackOutput packOutput, final CompletableFuture<Provider> registries, final ExistingFileHelper existingFileHelper) {
 *          return new EnhancedAdvancementProvider(packOutput, registries, existingFileHelper, List.of(<sub providers>));
 *      }
 *  }
 * }
 * </pre>
 *
 * @implNote When using as a {@link ExtraLanguageProvider} this provider must be <strong>run first</strong>
 */
public final class EnhancedAdvancementProvider implements ExtraLanguageProvider, DataProvider {

	private static final ResourceType ADVANCEMENT = new ResourceType(PackType.SERVER_DATA, ".json", "advancement");

	private final PackOutput.PathProvider pathProvider;
	private final CompletableFuture<HolderLookup.Provider> registries;
	private final ExistingFileHelper existingFileHelper;
	private final List<AdvancementSubProvider> subProviders;
	private final List<LanguageTranslation> languageTranslations = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param registries The registries
	 * @param existingFileHelper The existing file helper
	 * @param subProviders The sub providers actually adding advancements
	 */
	public EnhancedAdvancementProvider(final PackOutput output, final CompletableFuture<Provider> registries,
			final ExistingFileHelper existingFileHelper, final List<AdvancementSubProvider> subProviders) {
		this.pathProvider = output.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
		this.registries = registries;
		this.existingFileHelper = existingFileHelper;
		this.subProviders = subProviders;
	}

	@Override
	public Stream<LanguageTranslation> extraTranslations() {
		return languageTranslations.stream();
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput output) {
		return this.registries.thenCompose(registries -> {
			final Set<ResourceLocation> set = new HashSet<>();
			final List<CompletableFuture<?>> list = new ArrayList<>();

			final var advancementOutput = new AdvancementOutput() {
				@Override
				public AdvancementHolder accept(final AdvancementHolder advancement, final ICondition... conditions) {
					if (!set.add(advancement.id())) throw new IllegalStateException("Duplicate advancement " + advancement.id());

					advancement.value().parent().ifPresent(parent -> {
						if (!existingFileHelper.exists(advancement.id(), ADVANCEMENT)) {
							throw new IllegalStateException(
									"The parent: '%s' of advancement '%s', has not been saved yet!".formatted(parent, advancement.id()));
						}
					});

					existingFileHelper.trackGenerated(advancement.id(), ADVANCEMENT);
					list.add(DataProvider.saveStable(output, registries, Advancement.CONDITIONAL_CODEC,
							Optional.of(new WithConditions<>(advancement.value(), conditions)), pathProvider.json(advancement.id())));
					return advancement;
				}

				@Override
				public void lang(final LanguageTranslation languageTranslation) {
					languageTranslations.add(languageTranslation);
				}
			};

			subProviders.forEach(subProvider -> subProvider.generate(advancementOutput, registries));

			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}