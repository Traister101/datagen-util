package mod.traister101.datagenutils.data.tfc;

import com.google.common.collect.ImmutableMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.data.DataManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Copy and paste of TFC's DataManagerProvider that isn't currently shipped in the mod
 */
public abstract class DataManagerProvider<T> implements DataProvider {

	protected final CompletableFuture<?> contentDone;
	private final DataManager<T> manager;
	private final CompletableFuture<HolderLookup.Provider> lookup;
	private final String modid;
	private final ImmutableMap.Builder<ResourceLocation, T> elements;
	private final PackOutput.PathProvider path;

	protected DataManagerProvider(final DataManager<T> manager, final PackOutput output, final String modid,
			final CompletableFuture<Provider> lookup) {
		this.manager = manager;
		this.modid = modid;
		this.lookup = lookup;
		this.elements = ImmutableMap.builder();
		this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, TerraFirmaCraft.MOD_ID + "/" + manager.getName());
		this.contentDone = new CompletableFuture<>();
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput output) {
		return beforeRun().thenCompose(provider -> {
			addData(provider);
			final Map<ResourceLocation, T> map = elements.buildOrThrow();
			manager.bindValues(map);
			contentDone.complete(null);
			return CompletableFuture.allOf(map.entrySet()
					.stream()
					.map(e -> DataProvider.saveStable(output, provider, manager.codec(), e.getValue(), path.json(e.getKey())))
					.toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public final String getName() {
		return "Data Manager (" + manager.getName() + ")";
	}

	public CompletableFuture<?> output() {
		return contentDone;
	}

	protected final void add(final String name, final T value) {
		final var resourceLocation = ResourceLocation.fromNamespaceAndPath(modid, name);
		add(resourceLocation, value);
	}

	protected final void add(final ResourceLocation name, final T value) {
		elements.put(name, value);
	}

	protected final void add(final DataManager.Reference<T> reference, final T value) {
		elements.put(reference.id(), value);
	}

	protected CompletableFuture<HolderLookup.Provider> beforeRun() {
		return lookup;
	}

	protected abstract void addData(final HolderLookup.Provider provider);
}