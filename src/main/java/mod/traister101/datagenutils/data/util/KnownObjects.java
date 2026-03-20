package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.registries.*;

import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
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
	 * The objects registry name
	 */
	private final Function<T, ResourceLocation> locationFunction;
	/**
	 * An iterable of the known objects
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
	 */
	@Contract(pure = true)
	public KnownObjects(final String name, final Function<T, String> keyFunction, final Function<T, ResourceLocation> locationFunction,
			final Collection<T> knownObjects) {
		this.name = name;
		this.keyFunction = keyFunction;
		this.locationFunction = locationFunction;
		this.knownObjects = knownObjects;
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
		return new KnownObjects<>("DeferredRegister[" + register.getRegistryName() + "]", keyFunction.compose(DeferredHolder::get),
				DeferredHolder::getId, register.getEntries());
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
		final Function<Reference<T>, ResourceLocation> holderToLocation = holder -> holder.key().location();
		final var knownObjects = registryProvider.lookupOrThrow(registryKey)
				.listElements()
				.filter(holder -> holder.key().location().getNamespace().equals(modid))
				.toList();
		return new KnownObjects<>("Registry [" + registryKey.location() + "]", keyFunction.compose(holderToLocation), holderToLocation, knownObjects);
	}

	/**
	 * {@return The known objects}
	 */
	@ApiStatus.Internal
	public Stream<KnownObject> knownObjects() {
		return knownObjects.stream().map(t -> new KnownObject(keyFunction.apply(t), locationFunction.apply(t)));
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
		 * The id
		 */
		ResourceLocation id;

		/**
		 * The constructor
		 *
		 * @param langKey The language key
		 * @param id The id
		 */
		@Contract(pure = true)
		public KnownObject(final String langKey, final ResourceLocation id) {
			this.langKey = langKey;
			this.id = id;
		}
	}
}
