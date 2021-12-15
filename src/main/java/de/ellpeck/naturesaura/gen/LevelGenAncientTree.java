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
        var height = rand.nextInt(3) + 5;
        var trunkTop = pos.above(height);

        this.setBlock(level, pos, Blocks.AIR.defaultBlockState());
        //Roots
        var rootsAmount = rand.nextInt(4) + 5;
        for (var i = 0; i < rootsAmount; i++) {
            var length = rand.nextInt(3) + 3;
            var angle = 2F * (float) Math.PI * (i / (float) rootsAmount);
            var x = (float) Math.sin(angle) * length;
            var z = (float) Math.cos(angle) * length;

            var goal = pos.offset(x, 0, z);
            while (level.isStateAtPosition(goal, state -> state.getMaterial().isReplaceable())) {
                goal = goal.below();
                if (goal.distSqr(pos) >= 10 * 10)
                    break;
            }
            this.makeBranch(level, pos.above(rand.nextInt(1)), goal, ModBlocks.ANCIENT_BARK.defaultBlockState(), false);
        }

        //Trunk
        for (var x = 0; x <= 1; x++) {
            for (var z = 0; z <= 1; z++) {
                for (var i = height - (x + z) * (rand.nextInt(2) + 2); i >= 0; i--) {
                    var goal = pos.offset(x, i, z);
                    if (!level.isStateAtPosition(goal, s -> !TreeFeature.validTreePos(level, goal))) {
                        this.setBlock(level, goal, ModBlocks.ANCIENT_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Axis.Y));
                    }
                }
            }
        }
        this.makeLeaves(level, trunkTop.above(rand.nextInt(2) - 1), ModBlocks.ANCIENT_LEAVES.defaultBlockState(), rand.nextInt(2) + 3, rand);

        //Branches
        var branchAmount = rand.nextInt(3) + 4;
        for (var i = 0; i < branchAmount; i++) {
            var length = rand.nextInt(2) + 3;
            var angle = 2F * (float) Math.PI * (i / (float) branchAmount);
            var x = (float) Math.sin(angle) * length;
            var z = (float) Math.cos(angle) * length;

            var goal = trunkTop.offset(x, rand.nextInt(3) + 1, z);
            this.makeBranch(level, trunkTop, goal, ModBlocks.ANCIENT_LOG.defaultBlockState(), true);
            this.makeLeaves(level, goal, ModBlocks.ANCIENT_LEAVES.defaultBlockState(), rand.nextInt(2) + 2, rand);
        }

        return true;
    }

    private void makeBranch(WorldGenLevel level, BlockPos first, BlockPos second, BlockState state, boolean hasAxis) {
        var pos = second.offset(-first.getX(), -first.getY(), -first.getZ());
        var length = this.getHighestCoord(pos);
        var stepX = (float) pos.getX() / (float) length;
        var stepY = (float) pos.getY() / (float) length;
        var stepZ = (float) pos.getZ() / (float) length;

        for (var i = 0; i <= length; i++) {
            var goal = first.offset(0.5F + i * stepX, 0.5F + i * stepY, 0.5F + i * stepZ);
            if (!level.isStateAtPosition(goal, s -> !TreeFeature.validTreePos(level, goal))) {
                if (hasAxis) {
                    var axis = this.getLogAxis(first, goal);
                    this.setBlock(level, goal, state.setValue(RotatedPillarBlock.AXIS, axis));
                } else {
                    this.setBlock(level, goal, state);
                }
            }
        }
    }

    private void makeLeaves(WorldGenLevel level, BlockPos pos, BlockState state, int radius, Random rand) {
        for (var x = -radius; x <= radius; x++) {
            for (var y = -radius; y <= radius; y++) {
                for (var z = -radius; z <= radius; z++) {
                    var goal = pos.offset(x, y, z);
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
        var axis = Axis.Y;
        var x = Math.abs(goal.getX() - pos.getX());
        var y = Math.abs(goal.getZ() - pos.getZ());
        var highest = Math.max(x, y);
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
