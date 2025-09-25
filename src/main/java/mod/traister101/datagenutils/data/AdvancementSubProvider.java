package mod.traister101.datagenutils.data;

import mod.traister101.datagenutils.data.util.*;

import net.minecraft.core.HolderLookup.Provider;

/**
 * An interface used to generated advancements. This is similar to
 * vanilla's {@link net.minecraft.data.advancements.AdvancementSubProvider} however it's built to use
 * {@link AdvancementBuilder} rather than vanillas builder. You <i>can</i> use vanillas builder however you'll need to pass the id and advancement
 * object to the provided {@link AdvancementOutput}
 */
public interface AdvancementSubProvider {

	/**
	 * A method used to generate advancements for a mod
	 *
	 * @param output The advancement output
	 * @param registries A lookup for registries and their objects
	 */
	void generate(AdvancementOutput output, Provider registries);
}