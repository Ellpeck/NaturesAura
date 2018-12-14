package de.ellpeck.naturesaura;

import baubles.api.BaublesApi;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.blocks.multi.Multiblock;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;
import java.util.function.BiConsumer;

public class InternalHooks implements NaturesAuraAPI.IInternalHooks {
    @Override
    public boolean extractAuraFromPlayer(EntityPlayer player, int amount, boolean simulate) {
        return this.auraPlayerInteraction(player, amount, true, simulate);
    }

    @Override
    public boolean insertAuraIntoPlayer(EntityPlayer player, int amount, boolean simulate) {
        return this.auraPlayerInteraction(player, amount, false, simulate);
    }

    private boolean auraPlayerInteraction(EntityPlayer player, int amount, boolean extract, boolean simulate) {
        if (extract && player.capabilities.isCreativeMode)
            return true;

        if (Compat.baubles) {
            IItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (!stack.isEmpty() && stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                    IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null);
                    if (extract)
                        amount -= container.drainAura(amount, simulate);
                    else
                        amount -= container.storeAura(amount, simulate);
                    if (amount <= 0)
                        return true;
                }
            }
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null);
                if (extract)
                    amount -= container.drainAura(amount, simulate);
                else
                    amount -= container.storeAura(amount, simulate);
                if (amount <= 0)
                    return true;
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
        Vector3f dir = new Vector3f(endX - startX, endY - startY, endZ - startZ);
        if (dir.length() > 0) {
            int maxAge = (int) (dir.length() / speed);
            dir.normalise();

            this.spawnMagicParticle(startX, startY, startZ,
                    dir.x * speed, dir.y * speed, dir.z * speed,
                    color, scale, maxAge, 0F, false, false);
        }
    }

    @Override
    public IMultiblock createMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        return new Multiblock(name, pattern, rawMatchers);
    }

    @Override
    public boolean isEffectInhibited(World world, BlockPos pos, ResourceLocation name, int radius) {
        List<EntityEffectInhibitor> inhibitors = world.getEntitiesWithinAABB(
                EntityEffectInhibitor.class,
                new AxisAlignedBB(pos).grow(radius),
                entity -> entity.getDistanceSq(pos) <= radius * radius && name.equals(entity.getInhibitedEffect()));
        return !inhibitors.isEmpty();
    }

    @Override
    public void getAuraSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":getSpotsInArea");
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                if (Helper.isChunkLoaded(world, x, z)) {
                    Chunk chunk = world.getChunk(x, z);
                    if (chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
                        IAuraChunk auraChunk = chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
                        auraChunk.getSpotsInArea(pos, radius, consumer);
                    }
                }
            }
        }
        world.profiler.endSection();
    }

    @Override
    public int getAuraInArea(World world, BlockPos pos, int radius) {
        MutableInt result = new MutableInt(IAuraChunk.DEFAULT_AURA);
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> result.add(drainSpot));
        return result.intValue();
    }

    @Override
    public int triangulateAuraInArea(World world, BlockPos pos, int radius) {
        MutableFloat result = new MutableFloat(IAuraChunk.DEFAULT_AURA);
        IAuraChunk.getSpotsInArea(world, pos, radius, (blockPos, spot) -> {
            float percentage = 1F - (float) pos.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) / radius;
            result.add(spot * percentage);
        });
        return result.intValue();
    }

    @Override
    public BlockPos getLowestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt lowestAmount = new MutableInt(Integer.MAX_VALUE);
        MutableObject<BlockPos> lowestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> {
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
    public BlockPos getHighestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt highestAmount = new MutableInt(Integer.MIN_VALUE);
        MutableObject<BlockPos> highestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> {
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
