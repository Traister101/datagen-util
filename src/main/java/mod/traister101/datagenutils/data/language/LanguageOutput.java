package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.util.LanguageTranslation;

import java.util.function.Supplier;

/**
 * An object that accepts language translations which will be output to a file
 *
 * @param <T> The type this language output handles
 */
public interface LanguageOutput<T> {

	/**
	 * @param translation The translation to add
	 */
	void add(LanguageTranslation translation);

	/**
	 * Get the name from the registry name of the object.
	 */
	void simple(T t);

	/**
	 * @implNote Delegates to {@link #simple(Object)}
	 */
	@SuppressWarnings("unused")
	default void simple(Supplier<? extends T> t) {
		simple(t.get());
	}

	/**
	 * @param t The object
	 * @param name The name of the object
	 */
	void add(T t, String name);

	/**
	 * @param t The object supplier (typically some sort of {@link net.neoforged.neoforge.registries.DeferredHolder DeferredHolder})
	 * @param name The name of the object
	 *
	 * @implNote Delegates to {@link #add(Object, String)}
	 */
	default void add(Supplier<? extends T> t, String name) {
		add(t.get(), name);
	}
}
