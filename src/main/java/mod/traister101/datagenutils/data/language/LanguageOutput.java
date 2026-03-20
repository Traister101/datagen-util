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
	 * Adds a translation
	 *
	 * @param translation The translation to add
	 */
	void add(LanguageTranslation translation);

	/**
	 * Adds a simple translation generated from the objects registry name
	 *
	 * @param t The object
	 */
	void simple(T t);

	/**
	 * Adds a simple translation generated from the objects registry name
	 *
	 * @param t The object supplier (typically some sort of {@link net.neoforged.neoforge.registries.DeferredHolder DeferredHolder})
	 *
	 * @implNote Delegates to {@link #simple(Object)}
	 */
	@SuppressWarnings("unused")
	default void simple(Supplier<? extends T> t) {
		simple(t.get());
	}

	/**
	 * Adds a translation
	 *
	 * @param t The object
	 * @param name The name of the object
	 */
	void add(T t, String name);

	/**
	 * Adds a translation
	 *
	 * @param t The object supplier (typically some sort of {@link net.neoforged.neoforge.registries.DeferredHolder DeferredHolder})
	 * @param name The name of the object
	 *
	 * @implNote Delegates to {@link #add(Object, String)}
	 */
	default void add(Supplier<? extends T> t, String name) {
		add(t.get(), name);
	}
}
