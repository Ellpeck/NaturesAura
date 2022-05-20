package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.blocks.multi.Multiblock;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class InternalHooks implements NaturesAuraAPI.IInternalHooks {

    @Override
    public boolean extractAuraFromPlayer(Player player, int amount, boolean simulate) {
        return this.auraPlayerInteraction(player, amount, true, simulate);
    }

    @Override
    public boolean insertAuraIntoPlayer(Player player, int amount, boolean simulate) {
        return this.auraPlayerInteraction(player, amount, false, simulate);
    }

    private boolean auraPlayerInteraction(Player player, int amount, boolean extract, boolean simulate) {
        if (extract && player.isCreative())
            return true;
        var stack = Helper.getEquippedItem(s -> s.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER).isPresent(), player);
        if (!stack.isEmpty()) {
            var container = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER).orElse(null);
            if (extract) {
                return container.drainAura(amount, simulate) > 0;
            } else {
                return container.storeAura(amount, simulate) > 0;
            }
        }
        return false;
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        NaturesAura.proxy.spawnMagicParticle(posX, posY, posZ, motionX, motionY, motionZ, color, scale, maxAge, gravity, collision, fade);
    }

    @Override
    public void spawnParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {
        var dir = new Vec3(endX - startX, endY - startY, endZ - startZ);
        var length = dir.length();
        if (length > 0) {
            dir = dir.normalize();
            this.spawnMagicParticle(startX, startY, startZ,
                    dir.x * speed, dir.y * speed, dir.z * speed,
                    color, scale, (int) (length / speed), 0F, false, false);
        }
    }

    @Override
    public void setParticleDepth(boolean depth) {
        NaturesAura.proxy.setParticleDepth(depth);
    }

    @Override
    public void setParticleSpawnRange(int range) {
        NaturesAura.proxy.setParticleSpawnRange(range);
    }

    @Override
    public void setParticleCulling(boolean cull) {
        NaturesAura.proxy.setParticleCulling(cull);
    }

    @Override
    public IMultiblock createMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        return new Multiblock(name, pattern, rawMatchers);
    }

    @Override
    public List<Tuple<Vec3, Integer>> getActiveEffectPowders(Level level, AABB area, ResourceLocation name) {
        List<Tuple<Vec3, Integer>> found = new ArrayList<>();
        for (var powder : ((LevelData) ILevelData.getLevelData(level)).effectPowders.get(name))
            if (area.contains(powder.getA()))
                found.add(powder);
        return found;
    }

    @Override
    public boolean isEffectPowderActive(Level level, BlockPos pos, ResourceLocation name) {
        var posVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        var powders = this.getActiveEffectPowders(level, new AABB(pos).inflate(64), name);
        for (var powder : powders) {
            var bounds = Helper.aabb(powder.getA()).inflate(powder.getB());
            if (bounds.contains(posVec))
                return true;
        }
        return false;
    }

    @Override
    public void getAuraSpotsInArea(Level level, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        Helper.getAuraChunksWithSpotsInArea(level, pos, radius, chunk -> chunk.getSpots(pos, radius, consumer));
    }

    @Override
    public int getSpotAmountInArea(Level level, BlockPos pos, int radius) {
        var result = new MutableInt();
        Helper.getAuraChunksWithSpotsInArea(level, pos, radius, chunk -> result.add(chunk.getAuraAndSpotAmount(pos, radius).getRight()));
        return result.intValue();
    }

    @Override
    public int getAuraInArea(Level level, BlockPos pos, int radius) {
        var result = new MutableInt(IAuraChunk.DEFAULT_AURA);
        Helper.getAuraChunksWithSpotsInArea(level, pos, radius, chunk -> result.add(chunk.getAuraAndSpotAmount(pos, radius).getLeft()));
        return result.intValue();
    }

    @Override
    public Pair<Integer, Integer> getAuraAndSpotAmountInArea(Level level, BlockPos pos, int radius) {
        var spots = new MutableInt();
        var aura = new MutableInt(IAuraChunk.DEFAULT_AURA);
        Helper.getAuraChunksWithSpotsInArea(level, pos, radius, chunk -> {
            var auraAndSpots = chunk.getAuraAndSpotAmount(pos, radius);
            aura.add(auraAndSpots.getLeft());
            spots.add(auraAndSpots.getRight());
        });
        return Pair.of(aura.intValue(), spots.intValue());
    }

    @Override
    public int triangulateAuraInArea(Level level, BlockPos pos, int radius) {
        var result = new MutableFloat(IAuraChunk.DEFAULT_AURA);
        IAuraChunk.getSpotsInArea(level, pos, radius, (blockPos, spot) -> {
            var percentage = 1F - (float) Math.sqrt(pos.distSqr(blockPos)) / radius;
            result.add(spot * percentage);
        });
        return result.intValue();
    }

    @Override
    public BlockPos getLowestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        var lowestAmount = new MutableInt(Integer.MAX_VALUE);
        var lowestSpot = new MutableObject<BlockPos>();
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> {
            if (drainSpot < lowestAmount.intValue()) {
                lowestAmount.setValue(drainSpot);
                lowestSpot.setValue(blockPos);
            }
        });
        var lowest = lowestSpot.getValue();
        if (lowest == null || lowestAmount.intValue() >= 0)
            lowest = defaultSpot;
        return lowest;
    }

    @Override
    public BlockPos getHighestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        var highestAmount = new MutableInt(Integer.MIN_VALUE);
        var highestSpot = new MutableObject<BlockPos>();
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> {
            if (drainSpot > highestAmount.intValue()) {
                highestAmount.setValue(drainSpot);
                highestSpot.setValue(blockPos);
            }
        });
        var highest = highestSpot.getValue();
        if (highest == null || highestAmount.intValue() <= 0)
            highest = defaultSpot;
        return highest;
    }
}
