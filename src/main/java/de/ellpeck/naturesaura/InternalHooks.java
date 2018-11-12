package de.ellpeck.naturesaura;

import baubles.api.BaublesApi;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.BiConsumer;

public class InternalHooks implements NaturesAuraAPI.IInternalHooks {
    @Override
    public boolean extractAuraFromPlayer(EntityPlayer player, int amount, boolean simulate) {
        if (player.capabilities.isCreativeMode)
            return true;

        if (Compat.baubles) {
            IItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (!stack.isEmpty() && stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                    amount -= stack.getCapability(NaturesAuraAPI.capAuraContainer, null).drainAura(amount, simulate);
                    if (amount <= 0)
                        return true;
                }
            }
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                amount -= stack.getCapability(NaturesAuraAPI.capAuraContainer, null).drainAura(amount, simulate);
                if (amount <= 0)
                    return true;
            }
        }

        return false;
    }

    @Override
    public void spawnMagicParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        NaturesAura.proxy.spawnMagicParticle(world, posX, posY, posZ, motionX, motionY, motionZ, color, scale, maxAge, gravity, collision, fade);
    }

    @Override
    public void getAuraSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {
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
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> result.add(drainSpot.intValue()));
        return result.intValue();
    }

    @Override
    public BlockPos getLowestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt lowestAmount = new MutableInt(Integer.MAX_VALUE);
        MutableObject<BlockPos> lowestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> {
            int amount = drainSpot.intValue();
            if (amount < lowestAmount.intValue()) {
                lowestAmount.setValue(amount);
                lowestSpot.setValue(blockPos);
            }
        });
        BlockPos lowest = lowestSpot.getValue();
        if (lowest == null)
            lowest = defaultSpot;
        return lowest;
    }

    @Override
    public BlockPos getHighestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableInt highestAmount = new MutableInt(Integer.MIN_VALUE);
        MutableObject<BlockPos> highestSpot = new MutableObject<>();
        this.getAuraSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> {
            int amount = drainSpot.intValue();
            if (amount > highestAmount.intValue()) {
                highestAmount.setValue(amount);
                highestSpot.setValue(blockPos);
            }
        });
        BlockPos highest = highestSpot.getValue();
        if (highest == null)
            highest = defaultSpot;
        return highest;
    }
}
