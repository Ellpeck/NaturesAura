package de.ellpeck.naturesaura.gen;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import java.util.Random;
import java.util.Set;

public class WorldGenAncientTree extends AbstractTreeFeature<TreeFeatureConfig> {

    // what the heck even is this
    public static final TreeFeatureConfig CONFIG = new TreeFeatureConfig.Builder(null, null, null).build();

    public WorldGenAncientTree() {
        super(d -> CONFIG);
    }

    @Override
    protected boolean func_225557_a_(IWorldGenerationReader world, Random rand, BlockPos pos, Set<BlockPos> cb1, Set<BlockPos> cb2, MutableBoundingBox box, TreeFeatureConfig config) {
        int height = rand.nextInt(3) + 5;
        BlockPos trunkTop = pos.up(height);

        this.setBlockState(world, pos, Blocks.AIR.getDefaultState());
        //Roots
        int rootsAmount = rand.nextInt(4) + 5;
        for (int i = 0; i < rootsAmount; i++) {
            int length = rand.nextInt(3) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) rootsAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = pos.add(x, 0, z);
            while (world.hasBlockState(goal, state -> state.getMaterial().isReplaceable())) {
                goal = goal.down();
                if (goal.distanceSq(pos) >= 10 * 10)
                    break;
            }
            this.makeBranch(cb1, world, pos.up(rand.nextInt(1)), goal, ModBlocks.ANCIENT_BARK.getDefaultState(), false);
        }

        //Trunk
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                for (int i = height - (x + z) * (rand.nextInt(2) + 2); i >= 0; i--) {
                    BlockPos goal = pos.add(x, i, z);
                    if (func_214587_a(world, goal)) {
                        this.setBlockState(world, goal, ModBlocks.ANCIENT_LOG.getDefaultState().with(LogBlock.AXIS, Axis.Y));
                        cb1.add(goal);
                    }
                }
            }
        }
        this.makeLeaves(cb2, world, trunkTop.up(rand.nextInt(2) - 1), ModBlocks.ANCIENT_LEAVES.getDefaultState(), rand.nextInt(2) + 3, rand);

        //Branches
        int branchAmount = rand.nextInt(3) + 4;
        for (int i = 0; i < branchAmount; i++) {
            int length = rand.nextInt(2) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) branchAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = trunkTop.add(x, rand.nextInt(3) + 1, z);
            this.makeBranch(cb1, world, trunkTop, goal, ModBlocks.ANCIENT_LOG.getDefaultState(), true);
            this.makeLeaves(cb2, world, goal, ModBlocks.ANCIENT_LEAVES.getDefaultState(), rand.nextInt(2) + 2, rand);
        }

        return true;
    }

    private void makeBranch(Set changedBlocks, IWorldGenerationReader world, BlockPos first, BlockPos second, BlockState state, boolean hasAxis) {
        BlockPos pos = second.add(-first.getX(), -first.getY(), -first.getZ());
        int length = this.getHighestCoord(pos);
        float stepX = (float) pos.getX() / (float) length;
        float stepY = (float) pos.getY() / (float) length;
        float stepZ = (float) pos.getZ() / (float) length;

        for (int i = 0; i <= length; i++) {
            BlockPos goal = first.add(0.5F + i * stepX, 0.5F + i * stepY, 0.5F + i * stepZ);
            if (func_214587_a(world, goal)) {
                if (hasAxis) {
                    Axis axis = this.getLogAxis(first, goal);
                    this.setBlockState(world, goal, state.with(LogBlock.AXIS, axis));
                } else {
                    this.setBlockState(world, goal, state);
                }
                changedBlocks.add(goal);
            }
        }
    }

    private void makeLeaves(Set changedBlocks, IWorldGenerationReader world, BlockPos pos, BlockState state, int radius, Random rand) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos goal = pos.add(x, y, z);
                    if (pos.distanceSq(goal) <= radius * radius + rand.nextInt(3) - 1) {
                        if (isAirOrLeaves(world, goal)) {
                            if (world.hasBlockState(goal, st -> {
                                Block block = st.getBlock();
                                return !(block instanceof LogBlock) && block != Blocks.DIRT && block != Blocks.GRASS;
                            })) {
                                this.setBlockState(world, goal, state);
                                changedBlocks.add(goal);
                            }
                        }
                    }
                }
            }
        }
    }

    private int getHighestCoord(BlockPos pos) {
        return Math.max(MathHelper.abs(pos.getX()), Math.max(MathHelper.abs(pos.getY()), MathHelper.abs(pos.getZ())));
    }

    private Axis getLogAxis(BlockPos pos, BlockPos goal) {
        Axis axis = Axis.Y;
        int x = Math.abs(goal.getX() - pos.getX());
        int y = Math.abs(goal.getZ() - pos.getZ());
        int highest = Math.max(x, y);
        if (highest > 0) {
            if (x == highest) {
                axis = Axis.X;
            } else if (y == highest) {
                axis = Axis.Z;
            }
        }
        return axis;
    }
}
