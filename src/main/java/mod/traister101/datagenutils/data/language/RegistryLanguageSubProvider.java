package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.util.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.HolderLookup.Provider;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Contract;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A language sub provider which handles static registry objects
 *
 * @param <T> The game object type such as {@link net.minecraft.world.item.Item Item} or {@link net.minecraft.world.level.block.Block Block}
 */
public abstract class RegistryLanguageSubProvider<T> implements EnhancedLanguageSubProvider {

	private final DeferredRegister<T> register;
	private final Function<T, String> keyFunction;

	/**
	 * The constructor
	 *
	 * @param register The register
	 * @param keyFunction A function to convert the registry object into a language key
	 */
	@Contract(pure = true)
	protected RegistryLanguageSubProvider(final DeferredRegister<T> register, final Function<T, String> keyFunction) {
		this.register = register;
		this.keyFunction = keyFunction;
	}

	@Override
	public KnownObjects<?> knownObjects(final Provider provider) {
		return KnownObjects.fromRegister(register, keyFunction);
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
			public void simple(final T t) {
				final var id = Objects.requireNonNull(register.getRegistry().get().getKey(t),
						"Object isn't present in the registry. Did you forget to register it?");
				add(t, LanguageTranslation.langify(id.getPath()));
			}

			@Override
			public void add(final T t, final String name) {
				add(LanguageTranslation.of(keyFunction.apply(t), name));
			}
		});
		return translations.stream();
	}

	/**
	 * Add translations
	 *
	 * @param output The language output
	 */
	@OverrideOnly
	protected abstract void addTranslations(LanguageOutput<T> output);
}