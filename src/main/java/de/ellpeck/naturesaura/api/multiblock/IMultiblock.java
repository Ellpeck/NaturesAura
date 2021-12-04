package de.ellpeck.naturesaura.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.function.BiFunction;

public interface IMultiblock {

    boolean isComplete(Level level, BlockPos center);

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
