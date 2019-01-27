package de.ellpeck.naturesaura.chunk.effect;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.IPlantable;
import org.apache.commons.lang3.mutable.MutableInt;

public class NiceLookingEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "nice_looking");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 0)
            return;
        MutableInt aura = new MutableInt();
        MutableInt spots = new MutableInt();
        IAuraChunk.getSpotsInArea(world, pos, 35, (otherSpot, otherAmount) -> {
            spots.add(1);
            aura.add(otherAmount);
        });
        int excess = aura.intValue();
        if (excess <= 0)
            return;
        int amount = Math.min(50, excess / 400);
        if (amount < 2)
            return;
        if (spots.intValue() > 1)
            amount = Math.max(2, amount / (spots.intValue() - 1));
        for (int i = amount + world.rand.nextInt(amount / 2); i > 1; i--) {
            if (world.rand.nextFloat() >= 0.25F)
                continue;
            int x = pos.getX() + world.rand.nextInt(32) - 16;
            int z = pos.getZ() + world.rand.nextInt(32) - 16;
            BlockPos plantPos = new BlockPos(x, world.getHeight(x, z) - 1, z);
            if (!world.isBlockLoaded(plantPos))
                continue;
            IBlockState state = world.getBlockState(plantPos);
            Block block = state.getBlock();
            if (block instanceof IGrowable || block instanceof IPlantable || block.isLeaves(state, world, plantPos))
                PacketHandler.sendToAllAround(world, plantPos, 32,
                        new PacketParticles(plantPos.getX(), plantPos.getY(), plantPos.getZ(), 21, excess));
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return type == NaturesAuraAPI.TYPE_OVERWORLD;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
