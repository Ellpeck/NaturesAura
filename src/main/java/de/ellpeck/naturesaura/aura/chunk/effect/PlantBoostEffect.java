package de.ellpeck.naturesaura.aura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class PlantBoostEffect implements IDrainSpotEffect {
    @Override
    public void update(World world, Chunk chunk, AuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        if (spot.intValue() <= 0)
            return;
        int aura = AuraChunk.getAuraInArea(world, pos, 25);
        if (aura <= 0)
            return;
        int amount = Math.min(45, Math.abs(aura) / 1000);
        if (amount <= 1)
            return;
        int dist = MathHelper.clamp(Math.abs(aura) / 1500, 5, 35);
        if (dist <= 0)
            return;

        for (int i = amount / 2 + world.rand.nextInt(amount / 2); i >= 0; i--) {
            int x = MathHelper.floor(pos.getX() + world.rand.nextGaussian() * dist);
            int z = MathHelper.floor(pos.getZ() + world.rand.nextGaussian() * dist);
            BlockPos plantPos = new BlockPos(x, world.getHeight(x, z), z);
            if (plantPos.distanceSq(pos) <= dist * dist && world.isBlockLoaded(plantPos)) {
                IBlockState state = world.getBlockState(plantPos);
                Block block = state.getBlock();
                if (block instanceof IGrowable && block != Blocks.TALLGRASS && block != Blocks.GRASS) {
                    IGrowable growable = (IGrowable) block;
                    if (growable.canGrow(world, plantPos, state, false)) {
                        growable.grow(world, world.rand, plantPos, state);

                        BlockPos closestSpot = AuraChunk.getHighestSpot(world, plantPos, 25, pos);
                        AuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 25);

                        PacketHandler.sendToAllAround(world, plantPos, 32,
                                new PacketParticles(plantPos.getX(), plantPos.getY(), plantPos.getZ(), 6));
                    }
                }
            }
        }
    }
}
