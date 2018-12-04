package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SpreadEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "spread");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (Math.abs(spot) < 10000)
            return;
        boolean drain = spot > 0;
        int toMove = 7200;
        while (toMove > 0) {
            BlockPos bestOffset = null;
            int bestAmount = drain ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos offset = pos.offset(facing, 15);
                int amount = IAuraChunk.getAuraInArea(world, offset, 14);
                if (drain ? amount < bestAmount : amount > bestAmount) {
                    bestAmount = amount;
                    bestOffset = offset;
                }
            }

            BlockPos bestPos = drain ? IAuraChunk.getLowestSpot(world, bestOffset, 14, bestOffset)
                    : IAuraChunk.getHighestSpot(world, bestOffset, 14, bestOffset);
            IAuraChunk bestChunk = IAuraChunk.getAuraChunk(world, bestPos);

            int moved;
            if (drain) {
                moved = bestChunk.storeAura(bestPos, 1200);
                auraChunk.drainAura(pos, moved);
            } else {
                moved = bestChunk.drainAura(bestPos, 1200);
                auraChunk.storeAura(pos, moved);
            }
            toMove -= moved;
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
