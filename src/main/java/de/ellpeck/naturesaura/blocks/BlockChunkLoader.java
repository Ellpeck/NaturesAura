package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityChunkLoader;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockChunkLoader extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    private static final VoxelShape SHAPE = box(4, 4, 4, 12, 12, 12);

    public BlockChunkLoader() {
        super("chunk_loader", BlockEntityChunkLoader.class, Properties.of(Material.STONE).strength(3F).sound(SoundType.STONE));
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
                        (pos.getX() - range) >> 4 << 4,
                        0,
                        (pos.getZ() - range) >> 4 << 4,
                        ((pos.getX() + range) >> 4 << 4) + 16,
                        level.getHeight(),
                        ((pos.getZ() + range) >> 4 << 4) + 16);
            }
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        if (!ModConfig.instance.chunkLoader.get())
            return;
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityChunkLoader) {
            var range = ((BlockEntityChunkLoader) tile).range();
            for (var i = Mth.ceil(range / 8F); i > 0; i--) {
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
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
