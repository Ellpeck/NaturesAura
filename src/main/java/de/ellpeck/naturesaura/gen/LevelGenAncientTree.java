package de.ellpeck.naturesaura.gen;

import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.Material;

import java.util.Random;

import static net.minecraft.core.Direction.Axis;

public class LevelGenAncientTree extends Feature<TreeConfiguration> {

    public LevelGenAncientTree() {
        super(Codec.unit(ModFeatures.Configured.ANCIENT_TREE.config()));
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> ctx) {
        var level = ctx.level();
        var pos = ctx.origin();
        var rand = ctx.random();
        int height = rand.nextInt(3) + 5;
        BlockPos trunkTop = pos.above(height);

        this.setBlock(level, pos, Blocks.AIR.defaultBlockState());
        //Roots
        int rootsAmount = rand.nextInt(4) + 5;
        for (int i = 0; i < rootsAmount; i++) {
            int length = rand.nextInt(3) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) rootsAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = pos.offset(x, 0, z);
            while (level.isStateAtPosition(goal, state -> state.getMaterial().isReplaceable())) {
                goal = goal.below();
                if (goal.distSqr(pos) >= 10 * 10)
                    break;
            }
            this.makeBranch(level, pos.above(rand.nextInt(1)), goal, ModBlocks.ANCIENT_BARK.defaultBlockState(), false);
        }

        //Trunk
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                for (int i = height - (x + z) * (rand.nextInt(2) + 2); i >= 0; i--) {
                    BlockPos goal = pos.offset(x, i, z);
                    if (!level.isStateAtPosition(goal, s -> !TreeFeature.validTreePos(level, goal))) {
                        this.setBlock(level, goal, ModBlocks.ANCIENT_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Axis.Y));
                    }
                }
            }
        }
        this.makeLeaves(level, trunkTop.above(rand.nextInt(2) - 1), ModBlocks.ANCIENT_LEAVES.defaultBlockState(), rand.nextInt(2) + 3, rand);

        //Branches
        int branchAmount = rand.nextInt(3) + 4;
        for (int i = 0; i < branchAmount; i++) {
            int length = rand.nextInt(2) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) branchAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = trunkTop.offset(x, rand.nextInt(3) + 1, z);
            this.makeBranch(level, trunkTop, goal, ModBlocks.ANCIENT_LOG.defaultBlockState(), true);
            this.makeLeaves(level, goal, ModBlocks.ANCIENT_LEAVES.defaultBlockState(), rand.nextInt(2) + 2, rand);
        }

        return true;
    }

    private void makeBranch(WorldGenLevel level, BlockPos first, BlockPos second, BlockState state, boolean hasAxis) {
        BlockPos pos = second.offset(-first.getX(), -first.getY(), -first.getZ());
        int length = this.getHighestCoord(pos);
        float stepX = (float) pos.getX() / (float) length;
        float stepY = (float) pos.getY() / (float) length;
        float stepZ = (float) pos.getZ() / (float) length;

        for (int i = 0; i <= length; i++) {
            BlockPos goal = first.offset(0.5F + i * stepX, 0.5F + i * stepY, 0.5F + i * stepZ);
            if (!level.isStateAtPosition(goal, s -> !TreeFeature.validTreePos(level, goal))) {
                if (hasAxis) {
                    Axis axis = this.getLogAxis(first, goal);
                    this.setBlock(level, goal, state.setValue(RotatedPillarBlock.AXIS, axis));
                } else {
                    this.setBlock(level, goal, state);
                }
            }
        }
    }

    private void makeLeaves(WorldGenLevel level, BlockPos pos, BlockState state, int radius, Random rand) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos goal = pos.offset(x, y, z);
                    if (pos.distSqr(goal) <= radius * radius + rand.nextInt(3) - 1) {
                        if (!level.isStateAtPosition(goal, s -> s.getMaterial() == Material.LEAVES)) {
                            if (level.isStateAtPosition(goal, st -> st.getMaterial() != Material.WOOD && st.getBlock() != Blocks.DIRT && st.getBlock() != Blocks.GRASS))
                                this.setBlock(level, goal, state);
                        }
                    }
                }
            }
        }
    }

    private int getHighestCoord(BlockPos pos) {
        return Math.max(Mth.abs(pos.getX()), Math.max(Mth.abs(pos.getY()), Mth.abs(pos.getZ())));
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
