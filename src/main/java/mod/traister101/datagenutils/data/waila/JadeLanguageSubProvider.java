package mod.traister101.datagenutils.data.waila;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import mod.traister101.datagenutils.data.language.EnhancedLanguageSubProvider;
import mod.traister101.datagenutils.data.util.*;
import snownee.jade.api.IJadeProvider;
import snownee.jade.gui.config.OptionsList.Entry;
import snownee.jade.impl.config.PluginConfig;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A language provider for Jade. This provider depends on Jade code and will automatically report missing lang keys for all jade config values.
 * There are also related helpers for easily generating lang such as {@link ConfigEntryBuilder} and {@link EnumConfigBuilder}
 */
public abstract class JadeLanguageSubProvider implements EnhancedLanguageSubProvider {

	/**
	 * The description lang key post fix
	 */
	public static final String DESCRIPTION_POST_FIX = "_desc";
	private final String modId;
	private final List<LanguageTranslation> translations = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param modId The mod id
	 */
	protected JadeLanguageSubProvider(final String modId) {
		this.modId = modId;
	}

	/**
	 * Helper to make a language key in the jade format
	 *
	 * @param key The initial key
	 *
	 * @return The language key
	 */
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

	/**
	 * Add the translations
	 */
	@OverrideOnly
	protected abstract void addTranslations();

	/**
	 * {@return a ConfigEntryBuilder for the provider}
	 *
	 * @param provider The jade provider
	 */
	@CheckReturnValue
	protected ConfigEntryBuilder provider(final IJadeProvider provider) {
		return config(provider.getUid());
	}

	/**
	 * {@return a ConfigEntryBuilder for the config id}
	 *
	 * @param configId The config id
	 */
	@CheckReturnValue
	protected ConfigEntryBuilder config(final ResourceLocation configId) {
		return new ConfigEntryBuilder(configId);
	}

	/**
	 * {@return a EnumConfigBuilder for the config id and provided enum values}
	 *
	 * @param configId The config id
	 * @param values The enum values
	 * @param <E> The enum type
	 */
	@SafeVarargs
	@CheckReturnValue
	@Contract("_, _ -> new")
	@SuppressWarnings({"unused", "varargs"})
	protected final <E extends Enum<E>> EnumConfigBuilder<E> enumConfig(final ResourceLocation configId, final E... values) {
		return new EnumConfigBuilder<>(configId, values);
	}

	/**
	 * {@return a EnumConfigBuilder for the config name and provided enum values}
	 *
	 * @param configId The config id
	 * @param values The enum values (supplier to support passing {@code EnumType::values}
	 * @param <E> The enum type
	 */
	@CheckReturnValue
	@SuppressWarnings("unused")
	protected <E extends Enum<E>> EnumConfigBuilder<E> enumConfig(final ResourceLocation configId, final Supplier<E[]> values) {
		return new EnumConfigBuilder<>(configId, values.get());
	}

	/**
	 * Adds a translation key manually
	 *
	 * @param key The key
	 * @param translation The translation
	 */
	protected void add(final String key, final String translation) {
		add(LanguageTranslation.of(key, translation));
	}

	/**
	 * Add a translation manually
	 *
	 * @param translation A translation
	 */
	protected void add(final LanguageTranslation translation) {
		translations.add(translation);
	}

	/**
	 * A builder for jade config entries
	 */
	@CanIgnoreReturnValue
	protected class ConfigEntryBuilder {

		/**
		 * The config id
		 */
		protected final ResourceLocation configId;

		/**
		 * The constructor
		 *
		 * @param configId The config id
		 */
		@Contract(pure = true)
		public ConfigEntryBuilder(final ResourceLocation configId) {
			this.configId = configId;
		}

		/**
		 * Adds a name for the config entry
		 *
		 * @param name The name
		 *
		 * @return {@code this}
		 */
		public ConfigEntryBuilder name(final String name) {
			add(LanguageTranslation.of(makeKey(configId.toLanguageKey()), name));
			return this;
		}

		/**
		 * Adds a description for the config entry. These are optional and are only used if they have been defined
		 *
		 * @param description The description
		 *
		 * @return {@code this}
		 */
		public ConfigEntryBuilder description(final String description) {
			add(LanguageTranslation.of(makeKey(configId.toLanguageKey()) + DESCRIPTION_POST_FIX, description));
			return this;
		}
	}

	/**
	 * A builder for a config enum value
	 *
	 * @param <E> The enum type
	 */
	protected class EnumConfigBuilder<E extends Enum<E>> extends ConfigEntryBuilder {

		private final E[] values;

		/**
		 * The constructor
		 *
		 * @param configId The config id
		 * @param values The enum values
		 */
		public EnumConfigBuilder(final ResourceLocation configId, final E[] values) {
			super(configId);
			this.values = values;
		}

		/**
		 * Define language for the enum values
		 *
		 * @param action The action
		 *
		 * @return {@code this}
		 */
		@SuppressWarnings("unused")
		public EnumConfigBuilder<E> defineValues(final BiConsumer<E, EnumValueConfigBuilder> action) {
			for (final var value : values) {
				action.accept(value, enumValue(value));
			}
			return this;
		}

		@Override
		public EnumConfigBuilder<E> name(final String name) {
			super.name(name);
			return this;
		}

		@Override
		public EnumConfigBuilder<E> description(final String description) {
			super.description(description);
			return this;
		}

		/**
		 * Get an enum config value builder
		 *
		 * @param value The enum value
		 *
		 * @return The enum config value builder
		 */
		@CheckReturnValue
		public EnumValueConfigBuilder enumValue(final E value) {
			return new EnumValueConfigBuilder(value);
		}

		/**
		 * A builder for naming and giving descriptions to enum config values
		 */
		public class EnumValueConfigBuilder {

			private final E value;

			private EnumValueConfigBuilder(final E value) {
				this.value = value;
			}

			/**
			 * Defines the name of the enum value
			 *
			 * @param name The name
			 *
			 * @return {@code this}
			 */
			public EnumValueConfigBuilder name(final String name) {
				add(LanguageTranslation.of(getKey(), name));
				return this;
			}

			/**
			 * Defines the description of the enum value
			 *
			 * @param description The description
			 *
			 * @return {@code this}
			 */
			@SuppressWarnings("unused")
			public EnumValueConfigBuilder description(final String description) {
				add(LanguageTranslation.of(getKey() + DESCRIPTION_POST_FIX, description));
				return this;
			}

			private String getKey() {
				return makeKey(configId.toLanguageKey() + "_" + this.value.name().toLowerCase(Locale.ENGLISH));
			}
		}
	}
}