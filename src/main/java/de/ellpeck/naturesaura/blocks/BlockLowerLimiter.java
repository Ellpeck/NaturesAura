package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityLowerLimiter;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderLowerLimiter;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.SoundType;

public class BlockLowerLimiter extends BlockContainerImpl implements ICustomBlockState, ITESRProvider<BlockEntityLowerLimiter> {

    public BlockLowerLimiter() {
        super("lower_limiter", BlockEntityLowerLimiter.class, Properties.of().strength(2F).sound(SoundType.STONE));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModBlockEntities.LOWER_LIMITER, RenderLowerLimiter::new);
    }
}
