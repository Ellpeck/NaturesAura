package de.ellpeck.naturesaura.gen;

import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class WorldGenNetherWartMushroom extends Feature<NoFeatureConfig> {

    public WorldGenNetherWartMushroom() {
        super(Codec.unit(IFeatureConfig.NO_FEATURE_CONFIG));
    }

    @Override
    public boolean func_241855_a(ISeedReader worldIn, ChunkGenerator gen, Random rand, BlockPos pos, NoFeatureConfig p_241855_5_) {
        int height = rand.nextInt(5) + 4;
        if (rand.nextInt(10) == 0)
            height += 5;

        // Check if the stem has space
        for (int i = 1; i < height; i++) {
            BlockPos offset = pos.up(i);
            if (worldIn.hasBlockState(offset, s -> !s.canBeReplacedByLogs(worldIn, offset)))
                return false;
        }

        // Place stem
        this.func_230367_a_(worldIn, pos, Blocks.AIR.getDefaultState());
        for (int i = 0; i < height; i++)
            this.placeIfPossible(worldIn, pos.up(i), Blocks.NETHER_WART_BLOCK);

        // Place hat
        int rad = 3;
        for (int x = -rad; x <= rad; x++) {
            for (int z = -rad; z <= rad; z++) {
                int absX = Math.abs(x);
                int absZ = Math.abs(z);
                if (absX <= 1 && absZ <= 1) {
                    this.placeIfPossible(worldIn, pos.add(x, height, z), ModBlocks.NETHER_WART_MUSHROOM);
                } else if (absX <= 2 && absZ <= 2 && absX != absZ) {
                    this.placeIfPossible(worldIn, pos.add(x, height - 1, z), ModBlocks.NETHER_WART_MUSHROOM);
                } else if (absX < rad - 1 || absZ < rad - 1 || absX == rad - 1 && absZ == rad - 1) {
                    this.placeIfPossible(worldIn, pos.add(x, height - 2, z), ModBlocks.NETHER_WART_MUSHROOM);
                }
            }
        }
        return true;
    }

    private void placeIfPossible(IWorld world, BlockPos pos, Block block) {
        if (world.hasBlockState(pos, s -> s.canBeReplacedByLogs(world, pos)))
            world.setBlockState(pos, block.getDefaultState(), 19);
    }
}
