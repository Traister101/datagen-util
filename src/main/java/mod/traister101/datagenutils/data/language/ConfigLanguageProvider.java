package mod.traister101.datagenutils.data.language;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import mod.traister101.datagenutils.data.util.*;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;

import net.minecraft.core.HolderLookup.Provider;

import org.jetbrains.annotations.ApiStatus.*;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * A language provider for Configuration
 */
@Experimental
public abstract class ConfigLanguageProvider implements EnhancedLanguageSubProvider {

	private final String modId;
	private final List<LanguageTranslation> translations = new ArrayList<>();

	/**
	 * The constructor
	 *
	 * @param modId The mod id
	 */
	public ConfigLanguageProvider(final String modId) {
		this.modId = modId;
	}

	@Override
	public @Nullable KnownObjects<?> knownObjects(final Provider provider) {
		final var entries = ModConfigs.getModConfigs(modId)
				.stream()
				.map(modConfig -> ((ModConfigSpec) modConfig.getSpec()))
				.map(ModConfigSpec::getSpec)
				.map(UnmodifiableConfig::entrySet)
				.flatMap(Collection::stream)
				.toList();
		return KnownObjects.create("Config", entry -> getTranslationKey(entry.<ValueSpec>getValue().getTranslationKey(), entry.getKey()),
				Entry::getKey, entries);
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
	 * Add a language translation for the given config value
	 *
	 * @param configValue The config value
	 * @param name The name for the config
	 *
	 * @see #add(ConfigValue, String, String)
	 */
	protected void add(final ConfigValue<?> configValue, final String name) {
		add(getTranslationKey(configValue), name);
	}

	/**
	 * Add a language translation for the given config value
	 *
	 * @param configValue The config value
	 * @param name The name for the config
	 * @param tooltip The config tooltip
	 */
	protected void add(final ConfigValue<?> configValue, final String name, final String tooltip) {
		final var translationKey = getTranslationKey(configValue);
		add(translationKey, name);
		add(translationKey + ".tooltip", tooltip);
	}

	private String getTranslationKey(final ConfigValue<?> configValue) {
		return getTranslationKey(configValue.getSpec().getTranslationKey(), String.join(".", configValue.getPath()));
	}

	private String getTranslationKey(final @Nullable String translationKey, final String fallbackKey) {
		return translationKey != null ? translationKey : modId + ".configuration." + fallbackKey;
	}

	private void add(final String translationKey, final String name) {
		translations.add(LanguageTranslation.of(translationKey, name));
	}
}