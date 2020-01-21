package de.ellpeck.naturesaura.api.internal;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

public class StubMultiblock implements IMultiblock {

    private static final ResourceLocation NAME = new ResourceLocation(NaturesAuraAPI.MOD_ID, "stub");

    @Override
    public boolean isComplete(IWorld world, BlockPos center) {
        return false;
    }

    @Override
    public boolean forEach(BlockPos center, char c, BiFunction<BlockPos, Matcher, Boolean> function) {
        return false;
    }

    @Override
    public BlockPos getStart(BlockPos center) {
        return BlockPos.ZERO;
    }

    @Override
    public char getChar(BlockPos offset) {
        return 0;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }

    @Override
    public Map<BlockPos, Matcher> getMatchers() {
        return Collections.emptyMap();
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    public char[][][] getRawPattern() {
        return new char[0][0][0];
    }
}
