package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class BlockNetherGrass extends BlockImpl implements ICustomBlockState, BonemealableBlock {

    public BlockNetherGrass() {
        super("nether_grass", Properties.copy(Blocks.NETHERRACK).randomTicks());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel levelIn, BlockPos pos, Random random) {
        var up = pos.above();
        var upState = levelIn.getBlockState(up);
        if (upState.isFaceSturdy(levelIn, up, Direction.DOWN))
            levelIn.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.mcLoc("block/netherrack"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter levelIn, BlockPos pos, BlockState state, boolean isClient) {
        return levelIn.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level levelIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random rand, BlockPos pos, BlockState state) {
        var blockpos = pos.above();
        var blockstate = Blocks.GRASS.defaultBlockState();

        for (var i = 0; i < 128; ++i) {
            var blockpos1 = blockpos;
            var j = 0;

            while (true) {
                if (j >= i / 16) {
                    var blockstate2 = level.getBlockState(blockpos1);
                    if (blockstate2.getBlock() == blockstate.getBlock() && rand.nextInt(10) == 0) {
                        ((BonemealableBlock) blockstate.getBlock()).performBonemeal(level, rand, blockpos1, blockstate2);
                    }

                    if (!blockstate2.isAir()) {
                        break;
                    }

                    if (blockstate.canSurvive(level, blockpos1)) {
                        level.setBlock(blockpos1, blockstate, 3);
                    }
                    break;
                }

                blockpos1 = blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
                if (level.getBlockState(blockpos1.below()).getBlock() != this || level.getBlockState(blockpos1).isCollisionShapeFullBlock(level, blockpos1)) {
                    break;
                }

                ++j;
            }
        }

    }
}
