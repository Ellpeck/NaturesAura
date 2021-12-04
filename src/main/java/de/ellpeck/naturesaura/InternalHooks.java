package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.blocks.multi.Multiblock;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.Level;
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
        ItemStack stack = Helper.getEquippedItem(s -> s.getCapability(NaturesAuraAPI.capAuraContainer).isPresent(), player);
        if (!stack.isEmpty()) {
            IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer).orElse(null);
            if (extract) {
                amount -= container.drainAura(amount, simulate);
            } else {
                amount -= container.storeAura(amount, simulate);
            }
            return amount <= 0;
        }
        return false;
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        NaturesAura.proxy.spawnMagicParticle(posX, posY, posZ, motionX, motionY, motionZ, color, scale, maxAge, gravity, collision, fade);
    }

    @Override
    public void spawnParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {
        Vector3d dir = new Vector3d(endX - startX, endY - startY, endZ - startZ);
        double length = dir.length();
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
    public List<Tuple<Vector3d, Integer>> getActiveEffectPowders(Level level, AxisAlignedBB area, ResourceLocation name) {
        List<Tuple<Vector3d, Integer>> found = new ArrayList<>();
        for (Tuple<Vector3d, Integer> powder : ((LevelData) ILevelData.getLevelData(level)).effectPowders.get(name))
            if (area.contains(powder.getA()))
                found.add(powder);
        return found;
    }

    @Override
    public boolean isEffectPowderActive(Level level, BlockPos pos, ResourceLocation name) {
        Vector3d posVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        List<Tuple<Vector3d, Integer>> powders = this.getActiveEffectPowders(level, new AxisAlignedBB(pos).grow(64), name);
        for (Tuple<Vector3d, Integer> powder : powders) {
            AxisAlignedBB bounds = Helper.aabb(powder.getA()).grow(powder.getB());
            if (bounds.contains(posVec))
                return true;
        }
        return false;
    }

    @Override
    public void getAuraSpotsInArea(Level level, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        Helper.getAuraChunksWithSpotsInArea(level, pos, radius, chunk -> chunk.getSpotsInArea(pos, radius, consumer));
    }

    @Override
    public int getSpotAmountInArea(Level level, BlockPos pos, int radius) {
        MutableInt result = new MutableInt();
        this.getAuraSpotsInArea(level, pos, radius, (blockpos, drainSpot) -> result.increment());
        return result.intValue();
    }

    @Override
    public int getAuraInArea(Level level, BlockPos pos, int radius) {
        MutableInt result = new MutableInt(IAuraChunk.DEFAULT_AURA);
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> result.add(drainSpot));
        return result.intValue();
    }

    @Override
    public Pair<Integer, Integer> getAuraAndSpotAmountInArea(Level level, BlockPos pos, int radius) {
        MutableInt spots = new MutableInt();
        MutableInt aura = new MutableInt(IAuraChunk.DEFAULT_AURA);
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> {
            aura.add(drainSpot);
            spots.increment();
        });
        return Pair.of(aura.intValue(), spots.intValue());
    }

    @Override
    public int triangulateAuraInArea(Level level, BlockPos pos, int radius) {
        MutableFloat result = new MutableFloat(IAuraChunk.DEFAULT_AURA);
        IAuraChunk.getSpotsInArea(level, pos, radius, (blockPos, spot) -> {
            float percentage = 1F - (float) Math.sqrt(pos.distanceSq(blockPos)) / radius;
            result.add(spot * percentage);
        });
        return result.intValue();
    }

    @Override
    public BlockPos getLowestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt lowestAmount = new MutableInt(Integer.MAX_VALUE);
        MutableObject<BlockPos> lowestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> {
            if (drainSpot < lowestAmount.intValue()) {
                lowestAmount.setValue(drainSpot);
                lowestSpot.setValue(blockPos);
            }
        });
        BlockPos lowest = lowestSpot.getValue();
        if (lowest == null || lowestAmount.intValue() >= 0)
            lowest = defaultSpot;
        return lowest;
    }

    @Override
    public BlockPos getHighestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt highestAmount = new MutableInt(Integer.MIN_VALUE);
        MutableObject<BlockPos> highestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(level, pos, radius, (blockPos, drainSpot) -> {
            if (drainSpot > highestAmount.intValue()) {
                highestAmount.setValue(drainSpot);
                highestSpot.setValue(blockPos);
            }
        });
        BlockPos highest = highestSpot.getValue();
        if (highest == null || highestAmount.intValue() <= 0)
            highest = defaultSpot;
        return highest;
    }
}
