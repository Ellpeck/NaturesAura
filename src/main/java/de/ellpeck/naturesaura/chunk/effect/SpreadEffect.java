package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SpreadEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "spread");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (Math.abs(spot) < 500000 || Math.abs(IAuraChunk.getAuraInArea(world, pos, 25)) < 2000000)
            return;
        boolean drain = spot > 0;
        int toMove = MathHelper.ceil(Math.abs(spot) * 0.72F);
        int perSide = toMove / 6;
        while (toMove > 0) {
            BlockPos bestOffset = null;
            int bestAmount = drain ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            for (Direction facing : Direction.values()) {
                BlockPos offset = pos.offset(facing, 15);
                if (world.isBlockLoaded(offset) && offset.getY() >= 0 && offset.getY() <= world.getHeight()) {
                    int amount = IAuraChunk.getAuraInArea(world, offset, 14);
                    if (drain ? amount < bestAmount : amount > bestAmount) {
                        bestAmount = amount;
                        bestOffset = offset;
                    }
                }
            }
            if (bestOffset == null)
                break;

            BlockPos bestPos = drain ? IAuraChunk.getLowestSpot(world, bestOffset, 14, bestOffset)
                    : IAuraChunk.getHighestSpot(world, bestOffset, 14, bestOffset);
            IAuraChunk bestChunk = IAuraChunk.getAuraChunk(world, bestPos);

            int moved;
            if (drain) {
                moved = bestChunk.storeAura(bestPos, perSide);
                auraChunk.drainAura(pos, moved);
            } else {
                moved = bestChunk.drainAura(bestPos, perSide);
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
