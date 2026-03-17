package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.util.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.HolderLookup.Provider;

import lombok.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A language sub provider which handles static registry objects
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RegistryLanguageSubProvider<T> implements EnhancedLanguageSubProvider {

	private final DeferredRegister<T> register;
	private final Function<T, String> keyFunction;

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

	protected abstract void addTranslations(LanguageOutput<T> output);
}