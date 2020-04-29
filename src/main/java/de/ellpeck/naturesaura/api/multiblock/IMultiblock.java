package de.ellpeck.naturesaura.api.multiblock;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.BiFunction;

public interface IMultiblock {

    boolean isComplete(World world, BlockPos center);

    boolean forEach(BlockPos center, char c, BiFunction<BlockPos, Matcher, Boolean> function);

    BlockPos getStart(BlockPos center);

    char getChar(BlockPos offset);

    ResourceLocation getName();

    Map<BlockPos, Matcher> getMatchers();

    int getWidth();

    int getHeight();

    int getDepth();

    int getXOffset();

    int getYOffset();

    int getZOffset();

    char[][][] getRawPattern();
}
