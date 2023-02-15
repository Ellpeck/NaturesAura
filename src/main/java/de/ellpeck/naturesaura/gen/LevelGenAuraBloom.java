package de.ellpeck.naturesaura.gen;

import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraBloom;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LevelGenAuraBloom extends Feature<NoneFeatureConfiguration> {

    private final Block block;
    private final int chance;
    private final boolean nether;

    public LevelGenAuraBloom(Block block, int chance, boolean nether) {
        super(Codec.unit(FeatureConfiguration.NONE));
        this.block = block;
        this.chance = chance;
        this.nether = nether;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        var levelIn = ctx.level();
        var pos = ctx.origin();
        var rand = ctx.random();
        if (rand.nextInt(this.chance) != 0)
            return false;
        var startX = pos.getX() + rand.nextInt(16);
        var startZ = pos.getZ() + rand.nextInt(16);
        var any = false;
        for (var i = Mth.nextInt(rand, 3, 8); i > 0; i--) {
            var offX = startX + Mth.nextInt(rand, -5, 5);
            var offZ = startZ + Mth.nextInt(rand, -5, 5);
            if (this.nether) {
                var y = Mth.nextInt(rand, 0, 128);
                for (var off = 0; off < 64; off++) {
                    // try to find a good location in both directions of the random pos
                    if (this.tryPlace(levelIn, new BlockPos(offX, y - off, offZ)) || this.tryPlace(levelIn, new BlockPos(offX, y + off, offZ))) {
                        any = true;
                        break;
                    }
                }
            } else {
                var placePos = new BlockPos(offX, levelIn.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offX, offZ), offZ);
                if (this.tryPlace(levelIn, placePos))
                    any = true;
            }
        }
        return any;
    }

    private boolean tryPlace(WorldGenLevel level, BlockPos pos) {
        var state = this.block.defaultBlockState();
        if (state.canSurvive(level, pos)) {
            level.setBlock(pos, state, 3);
            var tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityAuraBloom bloom)
                bloom.justGenerated = true;
            return true;
        }
        return false;
    }
}
