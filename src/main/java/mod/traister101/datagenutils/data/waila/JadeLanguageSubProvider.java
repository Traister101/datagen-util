package mod.traister101.datagenutils.data.waila;

import mod.traister101.datagenutils.data.language.EnhancedLanguageSubProvider;
import mod.traister101.datagenutils.data.util.*;
import snownee.jade.api.IJadeProvider;
import snownee.jade.gui.config.OptionsList.Entry;
import snownee.jade.impl.config.PluginConfig;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@AllArgsConstructor
public abstract class JadeLanguageSubProvider implements EnhancedLanguageSubProvider {

	public static final String DESCRIPTION_POST_FIX = "_desc";
	private final String modId;
	private final List<LanguageTranslation> translations = new ArrayList<>();

	protected static String makeKey(final String key) {
		return Entry.makeKey(key);
	}

	@Override
	public @Nullable KnownObjects<?> knownObjects(final Provider provider) {
		final var keys = PluginConfig.INSTANCE.getKeys(modId);
		return new KnownObjects<>("Jade Config entries", resourceLocation -> makeKey(resourceLocation.toLanguageKey()), Function.identity(), keys);
	}

	@Override
	public Stream<LanguageTranslation> translations() {
		addTranslations();
		return translations.stream();
	}

	protected abstract void addTranslations();

	@CheckReturnValue
	protected ConfigEntryBuilder provider(final IJadeProvider provider) {
		return config(provider.getUid());
	}

	@CheckReturnValue
	protected ConfigEntryBuilder config(final ResourceLocation configId) {
		return new ConfigEntryBuilder(configId);
	}

	@SafeVarargs
	@CheckReturnValue
	@SuppressWarnings({"unused", "varargs"})
	protected final <E extends Enum<E>> EnumConfigBuilder<E> enumConfig(final ResourceLocation configName, final E... values) {
		return new EnumConfigBuilder<>(configName, values);
	}

	@CheckReturnValue
	@SuppressWarnings("unused")
	protected <E extends Enum<E>> EnumConfigBuilder<E> enumConfig(final ResourceLocation configName, final Supplier<E[]> values) {
		return new EnumConfigBuilder<>(configName, values.get());
	}

	private void add(final LanguageTranslation translation) {
		translations.add(translation);
	}

	@AllArgsConstructor
	protected class ConfigEntryBuilder {

		protected final ResourceLocation configId;

		public void name(final String name) {
			add(LanguageTranslation.of(makeKey(configId.toLanguageKey()), name));
		}

		@SuppressWarnings("unused")
		public void description(final String description) {
			add(LanguageTranslation.of(makeKey(configId.toLanguageKey()) + DESCRIPTION_POST_FIX, description));
		}
	}

	protected class EnumConfigBuilder<E extends Enum<E>> extends ConfigEntryBuilder {

		private final E[] values;

		public EnumConfigBuilder(final ResourceLocation configName, final E[] values) {
			super(configName);
			this.values = values;
		}

		@SuppressWarnings("unused")
		public void defineValues(final BiConsumer<E, EnumValueConfigBuilder> action) {
			for (final var value : values) {
				action.accept(value, enumValue(value));
			}
		}

		public EnumValueConfigBuilder enumValue(final E value) {
			return new EnumValueConfigBuilder(value);
		}

		@AllArgsConstructor
		protected class EnumValueConfigBuilder {

			private final E value;

			@SuppressWarnings("unused")
			public void name(final String name) {
				add(LanguageTranslation.of(getKey(), name));
			}

			@SuppressWarnings("unused")
			public void description(final String description) {
				add(LanguageTranslation.of(getKey() + DESCRIPTION_POST_FIX, description));
			}

			private String getKey() {
				return makeKey(configId.toLanguageKey() + "_" + this.value.name().toLowerCase(Locale.ENGLISH));
			}
		}
	}
}