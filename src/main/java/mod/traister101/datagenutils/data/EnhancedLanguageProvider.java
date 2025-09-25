package mod.traister101.datagenutils.data;

import com.google.gson.JsonObject;
import mod.traister101.datagenutils.data.util.LanguageTranslation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.*;

import net.minecraft.data.*;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import lombok.*;
import org.jetbrains.annotations.Contract;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Smarter {@link LanguageProvider} that checks to make sure registered objects have lang
 */
public abstract class EnhancedLanguageProvider implements DataProvider {

	private final Map<String, String> data = new TreeMap<>();
	private final PackOutput output;
	private final String modid;
	private final String locale;
	private final ExtraLanguageProvider[] extraLanguageProviders;

	/**
	 * @param output The pack output
	 * @param modid The mod id
	 * @param locale The locale such as 'en_us'
	 * @param extraLanguageProviders Var arg extra language providers. <strong>IMPORTANT:</strong> Typically, these must run before the language
	 * provider see docs for the {@link ExtraLanguageProvider} in question
	 */
	public EnhancedLanguageProvider(final PackOutput output, final String modid, final String locale,
			final ExtraLanguageProvider... extraLanguageProviders) {
		this.output = output;
		this.modid = modid;
		this.locale = locale;
		this.extraLanguageProviders = extraLanguageProviders;
	}

	/**
	 * Add all translations (not already handled via {@link ExtraLanguageProvider})
	 */
	protected abstract void addTranslations();

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		Arrays.stream(extraLanguageProviders).flatMap(ExtraLanguageProvider::extraTranslations).forEach(this::add);
		addTranslations();
		knownRegistryContents().forEach(this::validate);

		if (!data.isEmpty()) {
			final var path = output.getOutputFolder(Target.RESOURCE_PACK).resolve(modid).resolve("lang").resolve(locale + ".json");
			return save(cache, path);
		}

		return CompletableFuture.allOf();
	}

	@Override
	public String getName() {
		return "Languages: " + locale + " for mod: " + modid;
	}

	/**
	 * @return A stream of known registry contents
	 */
	protected abstract Stream<KnownRegistryContents<?>> knownRegistryContents();

	public final void add(final LanguageTranslation languageTranslation) {
		add(languageTranslation.key(), languageTranslation.translation());
	}

	private void add(final String key, final String translation) {
		if (data.put(key, translation) != null) throw new IllegalStateException("Duplicate translation key " + key);
	}

	private <T> void validate(final KnownRegistryContents<T> contents) {
		final var registryName = contents.registryName;
		final var keyFunction = contents.keyFunction;
		final var locationFunction = contents.locationFunction;
		contents.knownObjects.forEach(t -> validateEntry(registryName, keyFunction.apply(t), locationFunction.apply(t)));
	}

	private void validateEntry(final ResourceLocation registryName, final String langKey, final ResourceLocation objectName) {
		if (!data.containsKey(langKey)) {
			throw new IllegalStateException(String.format(Locale.ROOT, "Missing lang entry for '%s' in '%s'", objectName, registryName));
		}
	}

	private CompletableFuture<?> save(final CachedOutput cache, final Path target) {
		final var json = new JsonObject();
		data.forEach(json::addProperty);

		return DataProvider.saveStable(cache, json, target);
	}

	/**
	 * Some external provider with extra language such as {@link AdvancementSubProvider}
	 */
	public interface ExtraLanguageProvider {

		/**
		 * A stream of extra translations
		 */
		Stream<LanguageTranslation> extraTranslations();
	}

	/**
	 * @param <T> The object type
	 */
	@Value
	@AllArgsConstructor
	protected static class KnownRegistryContents<T> {

		/**
		 * The registry name
		 */
		ResourceLocation registryName;
		/**
		 * The lang key function
		 */
		Function<T, String> keyFunction;
		/**
		 * The objects registry name
		 */
		Function<T, ResourceLocation> locationFunction;
		/**
		 * An iterable of the known objects
		 */
		Stream<T> knownObjects;

		/**
		 * @param register A deferred register of the known objects
		 * @param keyFunction The lang key function
		 * @param <T> The object type
		 */
		@Contract("_, _ -> new")
		public static <T> KnownRegistryContents<DeferredHolder<T, ? extends T>> of(final DeferredRegister<T> register,
				final Function<T, String> keyFunction) {
			return new KnownRegistryContents<>(register.getRegistryName(), keyFunction.compose(DeferredHolder::get), DeferredHolder::getId,
					register.getEntries().stream());
		}

		/**
		 * @param blockRegister A deferred register of the known blocks
		 */
		@Contract("_ -> new")
		public static KnownRegistryContents<DeferredHolder<Block, ? extends Block>> block(final DeferredRegister<Block> blockRegister) {
			return of(blockRegister, Block::getDescriptionId);
		}

		/**
		 * @param itemRegister A deferred register of the known items
		 */
		@Contract("_ -> new")
		public static KnownRegistryContents<DeferredHolder<Item, ? extends Item>> item(final DeferredRegister<Item> itemRegister) {
			return of(itemRegister, Item::getDescriptionId);
		}

		/**
		 * @param effectRegister A deferred register of the known effects
		 */
		@Contract("_ -> new")
		public static KnownRegistryContents<DeferredHolder<MobEffect, ? extends MobEffect>> effect(final DeferredRegister<MobEffect> effectRegister) {
			return of(effectRegister, MobEffect::getDescriptionId);
		}

		/**
		 * @param entityTypeRegister A deferred register of the known entity types
		 */
		@Contract("_ -> new")
		public static KnownRegistryContents<DeferredHolder<EntityType<?>, ? extends EntityType<?>>> entity(
				final DeferredRegister<EntityType<?>> entityTypeRegister) {
			return of(entityTypeRegister, EntityType::getDescriptionId);
		}
	}
}