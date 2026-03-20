package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

/**
 * A language provider for {@link Block}s
 */
public abstract class BlockLanguageProvider extends RegistryLanguageSubProvider<Block> {

	/**
	 * The constructor
	 *
	 * @param register The block register
	 */
	public BlockLanguageProvider(final DeferredRegister<Block> register) {
		super(register, Block::getDescriptionId);
	}
}