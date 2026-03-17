package mod.traister101.datagenutils.data.language;

import mod.traister101.datagenutils.data.util.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Nullable;
import java.util.stream.Stream;

/**
 * A sub provider, providing language for only a subset of translatable game objects like {@link Item}s or {@link Block}s
 */
public interface EnhancedLanguageSubProvider {

	/**
	 * {@return The known objects this provider handles}
	 *
	 * @param provider The registries
	 */
	@OverrideOnly
	@Nullable KnownObjects<?> knownObjects(Provider provider);

	/**
	 * {@return A stream of translations this provider generates}
	 */
	@OverrideOnly
	Stream<LanguageTranslation> translations();
}