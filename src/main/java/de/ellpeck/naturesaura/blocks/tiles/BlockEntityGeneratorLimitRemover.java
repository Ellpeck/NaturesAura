package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityGeneratorLimitRemover extends BlockEntityImpl {

    public BlockEntityGeneratorLimitRemover(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR_LIMIT_REMOVER, pos, state);
    }

}
