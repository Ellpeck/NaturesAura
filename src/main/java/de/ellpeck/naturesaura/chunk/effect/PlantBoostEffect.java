package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PlantBoostEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "plant_boost");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 30);
        if (aura < 15000)
            return;
        int amount = Math.min(45, Math.abs(aura) / 1000);
        if (amount <= 1)
            return;
        int dist = MathHelper.clamp(Math.abs(aura) / 1500, 5, 35);

        for (int i = amount / 2 + world.rand.nextInt(amount / 2); i >= 0; i--) {
            int x = MathHelper.floor(pos.getX() + world.rand.nextGaussian() * dist);
            int z = MathHelper.floor(pos.getZ() + world.rand.nextGaussian() * dist);
            BlockPos plantPos = new BlockPos(x, world.getHeight(x, z), z);
            if (plantPos.distanceSq(pos) <= dist * dist && world.isBlockLoaded(plantPos)) {
                if (NaturesAuraAPI.instance().isEffectPowderActive(world, plantPos, NAME))
                    continue;

                IBlockState state = world.getBlockState(plantPos);
                Block block = state.getBlock();
                if (block instanceof IGrowable &&
                        block != Blocks.TALLGRASS && block != Blocks.GRASS && block != Blocks.DOUBLE_PLANT) {
                    IGrowable growable = (IGrowable) block;
                    if (growable.canGrow(world, plantPos, state, false)) {
                        growable.grow(world, world.rand, plantPos, state);

                        BlockPos closestSpot = IAuraChunk.getHighestSpot(world, plantPos, 25, pos);
                        IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 35);

                        PacketHandler.sendToAllAround(world, plantPos, 32,
                                new PacketParticles(plantPos.getX(), plantPos.getY(), plantPos.getZ(), 6));
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.plantBoostEffect && type == NaturesAuraAPI.TYPE_OVERWORLD;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
