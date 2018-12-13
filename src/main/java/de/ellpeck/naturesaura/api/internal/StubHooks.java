package de.ellpeck.naturesaura.api.internal;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class StubHooks implements NaturesAuraAPI.IInternalHooks {
    @Override
    public boolean extractAuraFromPlayer(EntityPlayer player, int amount, boolean simulate) {
        return false;
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {

    }

    @Override
    public void spawnParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {

    }

    @Override
    public IMultiblock createMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        return new StubMultiblock();
    }

    @Override
    public boolean isEffectInhibited(World world, BlockPos pos, ResourceLocation name, int radius) {
        return false;
    }

    @Override
    public void getAuraSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {

    }

    @Override
    public int getAuraInArea(World world, BlockPos pos, int radius) {
        return IAuraChunk.DEFAULT_AURA;
    }

    @Override
    public int triangulateAuraInArea(World world, BlockPos pos, int radius) {
        return IAuraChunk.DEFAULT_AURA;
    }

    @Override
    public BlockPos getLowestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return BlockPos.ORIGIN;
    }

    @Override
    public BlockPos getHighestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return BlockPos.ORIGIN;
    }
}
