package de.ellpeck.naturesaura.gen;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraBloom;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class WorldGenAuraBloom extends Feature<NoFeatureConfig> {
    public WorldGenAuraBloom() {
        super(d -> IFeatureConfig.NO_FEATURE_CONFIG);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (rand.nextInt(60) != 0)
            return false;

        int startX = pos.getX() + rand.nextInt(16);
        int startZ = pos.getZ() + rand.nextInt(16);
        boolean any = false;
        for (int i = MathHelper.nextInt(rand, 3, 8); i > 0; i--) {
            int offX = startX + MathHelper.nextInt(rand, -5, 5);
            int offZ = startZ + MathHelper.nextInt(rand, -5, 5);
            BlockPos placePos = new BlockPos(offX, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offX, offZ), offZ);
            BlockState state = ModBlocks.AURA_BLOOM.getDefaultState();
            if (ModBlocks.AURA_BLOOM.isValidPosition(state, worldIn, placePos)) {
                worldIn.setBlockState(placePos, state, 3);

                TileEntity tile = worldIn.getTileEntity(placePos);
                if (tile instanceof TileEntityAuraBloom)
                    ((TileEntityAuraBloom) tile).justGenerated = true;
                any = true;
            }
        }
        return any;
    }
}
