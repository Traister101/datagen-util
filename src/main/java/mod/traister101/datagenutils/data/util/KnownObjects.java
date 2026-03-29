package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.registries.*;

import net.minecraft.core.*;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.*;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.*;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A simple object that references known registry objects
 *
 * @param <T> The object type
 */
public final class KnownObjects<T> {

	/**
	 * The name for the collection of known objects
	 */
	@Getter
	@Accessors(fluent = true)
	private final String name;
	/**
	 * The lang key function
	 */
	private final Function<T, String> keyFunction;
	/**
	 * The objects name, when applicable this should be its registry name
	 */
	private final Function<T, String> objectNameFunction;
	/**
	 * A collection of the known objects
	 */
	private final Collection<T> knownObjects;

	/**
	 * The constructor, you should generally use one of the two factory functions {@link #fromRegister(DeferredRegister, Function)} and
	 * {@link #dynamicRegistry(Provider, ResourceKey, Function, String)}
	 *
	 * @param name The name of the known objects
	 * @param keyFunction The function to convert the object to a language key
	 * @param locationFunction A function to convert from the object to an id
	 * @param knownObjects A collection of the known objects
	 *
	 * @deprecated Use {@link #create(String, Function, Function, Collection)}
	 */
	@Deprecated(since = "1.2.3", forRemoval = true)
	@Contract(pure = true)
	public KnownObjects(final String name, final Function<T, String> keyFunction, final Function<T, ResourceLocation> locationFunction,
			final Collection<T> knownObjects) {
		this.name = name;
		this.keyFunction = keyFunction;
		this.objectNameFunction = locationFunction.andThen(ResourceLocation::toString);
		this.knownObjects = knownObjects;
	}

	private KnownObjects(final String name, final Function<T, String> keyFunction, final Function<T, String> objectNameFunction,
			final Collection<T> knownObjects, boolean dummy) {
		this.name = name;
		this.keyFunction = keyFunction;
		this.objectNameFunction = objectNameFunction;
		this.knownObjects = knownObjects;
	}

	/**
	 * The standard factory. You should generally use one of the other two factory functions {@link #fromRegister(DeferredRegister, Function)} and
	 * {@link #dynamicRegistry(Provider, ResourceKey, Function, String)}
	 *
	 * @param name The name of the known objects. This should try and include the "object source" such as
	 * {@literal "DeferredRegister[<registry name>]"} for those sourced from a {@link DeferredRegister} or
	 * {@literal "DynamicRegistry[<registry name>]"} for those sourced from a dynamic registry
	 * @param keyFunction The function to convert the object to a language key
	 * @param objectNameFunction A function to convert from the object to its name. Typically, these should be namespaced usually via
	 * {@link ResourceLocation#toString()}
	 * @param objects A collection of the known objects
	 * @param <T> The object type
	 *
	 * @return A {@link KnownObjects} which handles the provided objects
	 */
	@Contract(value = "_, _, _, _ -> new", pure = true)
	public static <T> KnownObjects<T> create(final String name, final Function<T, String> keyFunction, final Function<T, String> objectNameFunction,
			final Collection<T> objects) {
		return new KnownObjects<>(name, keyFunction, objectNameFunction, objects, false);
	}

	/**
	 * Helper factory for a {@link KnownObjects} using a {@link DeferredRegister}
	 *
	 * @param register A deferred register of the known objects
	 * @param keyFunction The lang key function
	 * @param <T> The object type
	 *
	 * @return The {@link KnownObjects} for the registry
	 */
	@Contract("_, _ -> new")
	public static <T> KnownObjects<DeferredHolder<T, ? extends T>> fromRegister(final DeferredRegister<T> register,
			final Function<T, String> keyFunction) {
		return create("DeferredRegister[" + register.getRegistryName() + "]", keyFunction.compose(DeferredHolder::get), Holder::getRegisteredName,
				register.getEntries());
	}

	/**
	 * Helper factory for dynamic registries
	 *
	 * @param registryProvider The registry provider
	 * @param registryKey The registry key
	 * @param keyFunction The language key function
	 * @param modid The modid of the owning mod (IE you. dynamic registries contain vanilla objects in datagen)
	 * @param <T> The object type
	 *
	 * @return The {@link KnownObjects} for the dynamic registry
	 */
	@Contract("_, _, _, _ -> new")
	public static <T> KnownObjects<Reference<T>> dynamicRegistry(final Provider registryProvider, final ResourceKey<Registry<T>> registryKey,
			final Function<ResourceLocation, String> keyFunction, final String modid) {
		final var knownObjects = registryProvider.lookupOrThrow(registryKey)
				.listElements()
				.filter(holder -> holder.key().location().getNamespace().equals(modid))
				.toList();
		return create("DynamicRegistry[" + registryKey.location() + "]", keyFunction.compose(holder -> holder.key().location()),
				Holder::getRegisteredName, knownObjects);
	}

	/**
	 * {@return The known objects}
	 */
	@ApiStatus.Internal
	public Stream<KnownObject> knownObjects() {
		return knownObjects.stream().map(t -> new KnownObject(keyFunction.apply(t), objectNameFunction.apply(t)));
	}

	/**
	 * A known object
	 */
	@Value
	@ApiStatus.Internal
	@Accessors(fluent = true)
	public static class KnownObject {

		/**
		 * The language key
		 */
		String langKey;
		/**
		 * The name
		 */
		String name;

		/**
		 * The constructor
		 *
		 * @param langKey The language key
		 * @param name The name
		 */
		@Contract(pure = true)
		public KnownObject(final String langKey, final String name) {
			this.langKey = langKey;
			this.name = name;
		}
	}
}
