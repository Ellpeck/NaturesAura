package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityChunkLoader;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockChunkLoader extends BlockContainerImpl implements IVisualizable, ICustomBlockState, IPickaxeBreakable {

    private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

    public BlockChunkLoader() {
        super("chunk_loader", BlockEntityChunkLoader.class, Properties.of().strength(3F).sound(SoundType.STONE));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityChunkLoader) {
            var range = ((BlockEntityChunkLoader) tile).range();
            if (range > 0) {
                return new AABB(
                    pos.getX() - range >> 4 << 4,
                    level.getMinBuildHeight(),
                    pos.getZ() - range >> 4 << 4,
                    (pos.getX() + range >> 4 << 4) + 16,
                    level.getMaxBuildHeight(),
                    (pos.getZ() + range >> 4 << 4) + 16);
            }
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        if (!ModConfig.instance.chunkLoader.get())
            return;
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityChunkLoader loader && loader.canUseRightNow(loader.getAuraUsed())) {
            for (var i = Mth.ceil(loader.range() / 8F); i > 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + levelIn.random.nextFloat(), pos.getY() + levelIn.random.nextFloat(), pos.getZ() + levelIn.random.nextFloat(),
                    0, 0, 0, 0xa12dff, 1F + levelIn.random.nextFloat(), 100, 0, false, true);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0xc159f9;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockChunkLoader.SHAPE;
    }

    @Override
    public String getDescriptionId() {
        return ModConfig.instance.chunkLoader.get() ? super.getDescriptionId() : "block." + NaturesAura.MOD_ID + "." + this.getBaseName() + ".disabled";
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
