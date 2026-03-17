package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.AdvancementSubProvider;
import mod.traister101.datagenutils.data.util.LanguageTranslation;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import java.util.stream.Stream;

/**
 * Some external provider with extra language such as {@link AdvancementSubProvider}
 */
public interface ExtraLanguageProvider {

	/**
	 * A stream of extra translations
	 *
	 * @return A stream of extra translations
	 */
	// TODO this might need to return a future to actually be safe, vanilla as of 1.21.1 runs providers sequentially in the order they were registered
	@OverrideOnly
	Stream<LanguageTranslation> extraTranslations();
}