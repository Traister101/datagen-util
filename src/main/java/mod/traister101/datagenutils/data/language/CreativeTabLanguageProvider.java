package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;
import java.util.function.Function;

public abstract class CreativeTabLanguageProvider extends RegistryLanguageSubProvider<CreativeModeTab> {

	/**
	 * @param register The creative tab register
	 * @param keyFunction Creative Tabs have {@link CreativeModeTab#displayName} which isn't tied to registry name. All tabs in the provided register
	 * are expected to use the same naming pattern, vanilla uses {@code itemGroup.<tab name>}, an easy way to automatically do this for a
	 * {@link DeferredRegister} is in some register method to pass {@link CreativeModeTab.Builder#title(Component)}
	 * {@code Component.translatable(registryName.toLanguageKey("itemGroup"))}. This will produce tabs with lang keys matching vanillas pattern and
	 * automatically handled by {@link #vanillaLangKey(CreativeModeTab)}, or you can just use the single arg constructor
	 * {@link #CreativeTabLanguageProvider(DeferredRegister)}
	 */
	protected CreativeTabLanguageProvider(final DeferredRegister<CreativeModeTab> register, final Function<CreativeModeTab, String> keyFunction) {
		super(register, keyFunction);
	}

	/**
	 * @param register The creative tab register.
	 *
	 * @implNote Expects all tabs to use the vanilla {@code itemGroup.<tab name>} lang key pattern
	 */
	protected CreativeTabLanguageProvider(final DeferredRegister<CreativeModeTab> register) {
		this(register, CreativeTabLanguageProvider::vanillaLangKey);
	}

	/**
	 * @param tab The creative tab
	 *
	 * @return The language key for the given creative tab
	 */
	protected static String vanillaLangKey(final CreativeModeTab tab) {
		return languageKey(tab, id -> id.toLanguageKey("itemGroup"));
	}

	protected static String languageKey(final CreativeModeTab tab, final Function<ResourceLocation, String> tabIdToLanguageKey) {
		return tabIdToLanguageKey.apply(Objects.requireNonNull(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab), "Unregistered Tab"));
	}

	protected static Function<CreativeModeTab, String> languageKey(final Function<ResourceLocation, String> tabIdToLanguageKey) {
		return tab -> languageKey(tab, tabIdToLanguageKey);
	}
}