package de.ellpeck.naturesaura.gen;

import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.level.ISeedReader;
import net.minecraft.level.gen.ChunkGenerator;
import net.minecraft.level.gen.ILevelGenerationReader;
import net.minecraft.level.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.level.gen.feature.Feature;

import java.util.Random;

public class LevelGenAncientTree extends Feature<BaseTreeFeatureConfig> {

    // what the heck even is this
    public static final BaseTreeFeatureConfig CONFIG = new BaseTreeFeatureConfig.Builder(null, null, null, null, null).build();

    public LevelGenAncientTree() {
        super(Codec.unit(CONFIG));
    }

    @Override
    public boolean func_241855_a(ISeedReader level, ChunkGenerator generator, Random rand, BlockPos pos, BaseTreeFeatureConfig p_241855_5_) {
        int height = rand.nextInt(3) + 5;
        BlockPos trunkTop = pos.up(height);

        this.func_230367_a_(level, pos, Blocks.AIR.getDefaultState());
        //Roots
        int rootsAmount = rand.nextInt(4) + 5;
        for (int i = 0; i < rootsAmount; i++) {
            int length = rand.nextInt(3) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) rootsAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = pos.add(x, 0, z);
            while (level.hasBlockState(goal, state -> state.getMaterial().isReplaceable())) {
                goal = goal.down();
                if (goal.distanceSq(pos) >= 10 * 10)
                    break;
            }
            this.makeBranch(level, pos.up(rand.nextInt(1)), goal, ModBlocks.ANCIENT_BARK.getDefaultState(), false);
        }

        //Trunk
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                for (int i = height - (x + z) * (rand.nextInt(2) + 2); i >= 0; i--) {
                    BlockPos goal = pos.add(x, i, z);
                    if (!level.hasBlockState(goal, s -> !s.canBeReplacedByLogs(level, goal))) {
                        this.func_230367_a_(level, goal, ModBlocks.ANCIENT_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, Axis.Y));
                    }
                }
            }
        }
        this.makeLeaves(level, trunkTop.up(rand.nextInt(2) - 1), ModBlocks.ANCIENT_LEAVES.getDefaultState(), rand.nextInt(2) + 3, rand);

        //Branches
        int branchAmount = rand.nextInt(3) + 4;
        for (int i = 0; i < branchAmount; i++) {
            int length = rand.nextInt(2) + 3;
            float angle = 2F * (float) Math.PI * (i / (float) branchAmount);
            float x = (float) Math.sin(angle) * length;
            float z = (float) Math.cos(angle) * length;

            BlockPos goal = trunkTop.add(x, rand.nextInt(3) + 1, z);
            this.makeBranch(level, trunkTop, goal, ModBlocks.ANCIENT_LOG.getDefaultState(), true);
            this.makeLeaves(level, goal, ModBlocks.ANCIENT_LEAVES.getDefaultState(), rand.nextInt(2) + 2, rand);
        }

        return true;
    }

    private void makeBranch(ISeedReader level, BlockPos first, BlockPos second, BlockState state, boolean hasAxis) {
        BlockPos pos = second.add(-first.getX(), -first.getY(), -first.getZ());
        int length = this.getHighestCoord(pos);
        float stepX = (float) pos.getX() / (float) length;
        float stepY = (float) pos.getY() / (float) length;
        float stepZ = (float) pos.getZ() / (float) length;

        for (int i = 0; i <= length; i++) {
            BlockPos goal = first.add(0.5F + i * stepX, 0.5F + i * stepY, 0.5F + i * stepZ);
            if (!level.hasBlockState(goal, s -> !s.canBeReplacedByLogs(level, goal))) {
                if (hasAxis) {
                    Axis axis = this.getLogAxis(first, goal);
                    this.func_230367_a_(level, goal, state.with(RotatedPillarBlock.AXIS, axis));
                } else {
                    this.func_230367_a_(level, goal, state);
                }
            }
        }
    }

    private void makeLeaves(ILevelGenerationReader level, BlockPos pos, BlockState state, int radius, Random rand) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos goal = pos.add(x, y, z);
                    if (pos.distanceSq(goal) <= radius * radius + rand.nextInt(3) - 1) {
                        if (!level.hasBlockState(goal, s -> s.getMaterial() == Material.LEAVES)) {
                            if (level.hasBlockState(goal, st -> {
                                Block block = st.getBlock();
                                return st.getMaterial() != Material.WOOD && block != Blocks.DIRT && block != Blocks.GRASS;
                            })) {
                                this.func_230367_a_(level, goal, state);
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
