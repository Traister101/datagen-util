package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.util.*;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.item.JukeboxSong;

import lombok.*;
import java.util.ArrayList;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A language sub provider which handles dynamic registry objects (IE stuff you register via datapack like {@link JukeboxSong}s)
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DynamicRegistryLanguageSubProvider<T> implements EnhancedLanguageSubProvider {

	private final ResourceKey<Registry<T>> registryKey;
	private final Function<ResourceLocation, String> keyFunction;
	private final String modid;

	/**
	 * @deprecated Extend {@link DynamicRegistryLanguageSubProvider}
	 */
	@Deprecated(forRemoval = true)
	public static <T> DynamicRegistryLanguageSubProvider<T> of(final ResourceKey<Registry<T>> registryKey,
			final Function<ResourceLocation, String> keyFunction, final String modid, final Consumer<LanguageOutput<ResourceKey<T>>> translations) {
		return new DynamicRegistryLanguageSubProvider<>(registryKey, keyFunction, modid) {
			@Override
			protected void addTranslations(final LanguageOutput<ResourceKey<T>> output) {
				translations.accept(output);
			}
		};
	}

	@Deprecated(forRemoval = true)
	public static DynamicRegistryLanguageSubProvider<JukeboxSong> jukeboxSong(final String modid,
			final Consumer<LanguageOutput<ResourceKey<JukeboxSong>>> translations) {
		return of(Registries.JUKEBOX_SONG, songName -> Util.makeDescriptionId("jukebox_song", songName), modid, translations);
	}

	@Override
	public KnownObjects<?> knownObjects(final Provider provider) {
		return KnownObjects.dynamicRegistry(provider, registryKey, keyFunction, modid);
	}

	@Override
	public Stream<LanguageTranslation> translations() {
		final var translations = new ArrayList<LanguageTranslation>();
		addTranslations(new LanguageOutput<>() {
			@Override
			public void add(final LanguageTranslation translation) {
				translations.add(translation);
			}

			@Override
			public void simple(final ResourceKey<T> resourceKey) {
				final var id = resourceKey.location();
				add(resourceKey, LanguageTranslation.langify(id.getPath()));
			}

			@Override
			public void add(final ResourceKey<T> resourceKey, final String name) {
				final var id = resourceKey.location();
				add(LanguageTranslation.of(keyFunction.apply(id), name));
			}
		});
		return translations.stream();
	}

	protected abstract void addTranslations(LanguageOutput<ResourceKey<T>> output);
}
