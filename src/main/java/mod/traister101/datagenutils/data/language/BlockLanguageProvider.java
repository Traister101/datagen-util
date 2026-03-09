package mod.traister101.datagenutils.data.language;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public abstract class BlockLanguageProvider extends RegistryLanguageSubProvider<Block> {

	public BlockLanguageProvider(final DeferredRegister<Block> register) {
		super(register, Block::getDescriptionId);
	}
}