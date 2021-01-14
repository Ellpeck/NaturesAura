package de.ellpeck.naturesaura.gen;

import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraBloom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class WorldGenAuraBloom extends Feature<NoFeatureConfig> {

    private final Block block;
    private final int chance;
    private final boolean nether;

    public WorldGenAuraBloom(Block block, int chance, boolean nether) {
        super(Codec.unit(IFeatureConfig.NO_FEATURE_CONFIG));
        this.block = block;
        this.chance = chance;
        this.nether = nether;
    }

    @Override
    public boolean func_241855_a(ISeedReader worldIn, ChunkGenerator gen, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (rand.nextInt(this.chance) != 0)
            return false;
        int startX = pos.getX() + rand.nextInt(16);
        int startZ = pos.getZ() + rand.nextInt(16);
        boolean any = false;
        for (int i = MathHelper.nextInt(rand, 3, 8); i > 0; i--) {
            int offX = startX + MathHelper.nextInt(rand, -5, 5);
            int offZ = startZ + MathHelper.nextInt(rand, -5, 5);
            if (this.nether) {
                int y = MathHelper.nextInt(rand, 0, 128);
                for (int off = 0; off < 64; off++) {
                    // try to find a good location in both directions of the random pos
                    if (this.tryPlace(worldIn, new BlockPos(offX, y - off, offZ)) || this.tryPlace(worldIn, new BlockPos(offX, y + off, offZ))) {
                        any = true;
                        break;
                    }
                }
            } else {
                BlockPos placePos = new BlockPos(offX, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offX, offZ), offZ);
                if (this.tryPlace(worldIn, placePos))
                    any = true;
            }
        }
        return any;
    }

    private boolean tryPlace(ISeedReader world, BlockPos pos) {
        BlockState state = this.block.getDefaultState();
        if (this.block.isValidPosition(state, world, pos)) {
            world.setBlockState(pos, state, 3);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityAuraBloom)
                ((TileEntityAuraBloom) tile).justGenerated = true;
            return true;
        }
        return false;
    }
}
