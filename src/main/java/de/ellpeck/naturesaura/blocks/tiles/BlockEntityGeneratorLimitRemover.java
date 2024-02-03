package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockEntityGeneratorLimitRemover extends BlockEntityImpl {

    public BlockEntityGeneratorLimitRemover(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR_LIMIT_REMOVER, pos, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition, this.worldPosition.offset(1, 2, 1));
    }
}
