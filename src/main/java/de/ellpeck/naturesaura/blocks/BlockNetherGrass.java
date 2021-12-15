package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.BlockGetter;
import net.minecraft.level.Level;
import net.minecraft.level.server.ServerLevel;

import java.util.Random;

public class BlockNetherGrass extends BlockImpl implements ICustomBlockState, IGrowable {

    public BlockNetherGrass() {
        super("nether_grass", Properties.from(Blocks.NETHERRACK).tickRandomly());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel levelIn, BlockPos pos, Random random) {
        BlockPos up = pos.up();
        BlockState upState = levelIn.getBlockState(up);
        if (upState.isSolidSide(levelIn, up, Direction.DOWN))
            levelIn.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.mcLoc("block/netherrack"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    public boolean canGrow(BlockGetter levelIn, BlockPos pos, BlockState state, boolean isClient) {
        return levelIn.getBlockState(pos.up()).isAir(levelIn, pos.up());
    }

    @Override
    public boolean canUseBonemeal(Level levelIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerLevel level, Random rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.up();
        BlockState blockstate = Blocks.GRASS.getDefaultState();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    BlockState blockstate2 = level.getBlockState(blockpos1);
                    if (blockstate2.getBlock() == blockstate.getBlock() && rand.nextInt(10) == 0) {
                        ((IGrowable) blockstate.getBlock()).grow(level, rand, blockpos1, blockstate2);
                    }

                    if (!blockstate2.isAir()) {
                        break;
                    }

                    if (blockstate.isValidPosition(level, blockpos1)) {
                        level.setBlockState(blockpos1, blockstate, 3);
                    }
                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
                if (level.getBlockState(blockpos1.down()).getBlock() != this || level.getBlockState(blockpos1).hasOpaqueCollisionShape(level, blockpos1)) {
                    break;
                }

                ++j;
            }
        }

    }
}
