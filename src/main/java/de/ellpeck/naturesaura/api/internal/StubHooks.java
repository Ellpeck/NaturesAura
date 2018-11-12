package de.ellpeck.naturesaura.api.internal;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

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
    public void getAuraSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {

    }

    @Override
    public int getAuraInArea(World world, BlockPos pos, int radius) {
        return 0;
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
