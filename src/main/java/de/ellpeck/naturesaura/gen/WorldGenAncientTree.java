package de.ellpeck.naturesaura.gen;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenAncientTree extends WorldGenAbstractTree {

    public WorldGenAncientTree(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = rand.nextInt(3) + 5;
        BlockPos trunkTop = pos.up(height);

        //Roots
        int rootsAmount = rand.nextInt(4) + 5;
        for (int i = 0; i < rootsAmount; i++) {
            int length = rand.nextInt(3) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) rootsAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = pos.add(x, 0, z);
            while (!world.getBlockState(goal).isFullBlock()) {
                goal = goal.down();
            }
            this.makeBranch(world, pos.up(rand.nextInt(1)), goal, ModBlocks.ANCIENT_BARK.getDefaultState(), false);
        }

        //Trunk
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                for (int i = height - (x + z) * (rand.nextInt(2) + 2); i >= 0; i--) {
                    BlockPos goal = pos.add(x, i, z);
                    if (this.isReplaceable(world, goal)) {
                        this.setBlockAndNotifyAdequately(world, goal,
                                ModBlocks.ANCIENT_LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y));
                    }
                }
            }
        }
        this.makeLeaves(world, trunkTop.up(rand.nextInt(2) - 1),
                ModBlocks.ANCIENT_LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false), rand.nextInt(2) + 3, rand);

        //Branches
        int branchAmount = rand.nextInt(3) + 4;
        for (int i = 0; i < branchAmount; i++) {
            int length = rand.nextInt(2) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) branchAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = trunkTop.add(x, rand.nextInt(3) + 1, z);
            this.makeBranch(world, trunkTop, goal, ModBlocks.ANCIENT_LOG.getDefaultState(), true);
            this.makeLeaves(world, goal,
                    ModBlocks.ANCIENT_LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false), rand.nextInt(2) + 2, rand);
        }

        return true;
    }

    @Override
    protected boolean canGrowInto(Block blockType) {
        if (super.canGrowInto(blockType)) {
            return true;
        } else {
            Material material = blockType.getDefaultState().getMaterial();
            return material == Material.VINE || material == Material.PLANTS;
        }
    }

    private void makeBranch(World world, BlockPos first, BlockPos second, IBlockState state, boolean hasAxis) {
        BlockPos pos = second.add(-first.getX(), -first.getY(), -first.getZ());
        int length = this.getHighestCoord(pos);
        float stepX = (float) pos.getX() / (float) length;
        float stepY = (float) pos.getY() / (float) length;
        float stepZ = (float) pos.getZ() / (float) length;

        for (int i = 0; i <= length; i++) {
            BlockPos goal = first.add((0.5F + i * stepX), (0.5F + i * stepY), (0.5F + i * stepZ));
            if (this.isReplaceable(world, goal)) {
                if (hasAxis) {
                    EnumAxis axis = this.getLogAxis(first, goal);
                    this.setBlockAndNotifyAdequately(world, goal, state.withProperty(BlockLog.LOG_AXIS, axis));
                } else {
                    this.setBlockAndNotifyAdequately(world, goal, state);
                }
            }
        }
    }

    private void makeLeaves(World world, BlockPos pos, IBlockState state, int radius, Random rand) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos goal = pos.add(x, y, z);
                    if (pos.distanceSq(goal) <= radius * radius + rand.nextInt(3) - 1) {
                        if (this.isReplaceable(world, goal)) {
                            Block block = world.getBlockState(goal).getBlock();
                            if (!(block instanceof BlockLog) && block != Blocks.DIRT && block != Blocks.GRASS)
                                this.setBlockAndNotifyAdequately(world, goal, state);
                        }
                    }
                }
            }
        }
    }

    private int getHighestCoord(BlockPos pos) {
        return Math.max(MathHelper.abs(pos.getX()), Math.max(MathHelper.abs(pos.getY()), MathHelper.abs(pos.getZ())));
    }

    private EnumAxis getLogAxis(BlockPos pos, BlockPos goal) {
        EnumAxis axis = EnumAxis.Y;
        int x = Math.abs(goal.getX() - pos.getX());
        int y = Math.abs(goal.getZ() - pos.getZ());
        int highest = Math.max(x, y);
        if (highest > 0) {
            if (x == highest) {
                axis = EnumAxis.X;
            } else if (y == highest) {
                axis = EnumAxis.Z;
            }
        }
        return axis;
    }
}