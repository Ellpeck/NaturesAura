package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class SpreadEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "spread");

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (Math.abs(spot) < 500000 || Math.abs(IAuraChunk.getAuraInArea(level, pos, 25) - IAuraChunk.DEFAULT_AURA) < 1000000)
            return;
        var drain = spot > 0;
        var toMove = Mth.ceil(Math.abs(spot) * 0.72F);
        var perSide = toMove / 6;
        while (toMove > 0) {
            BlockPos bestOffset = null;
            var bestAmount = drain ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            for (var facing : Direction.values()) {
                var offset = pos.relative(facing, 15);
                if (level.isLoaded(offset) && offset.getY() >= level.getMinBuildHeight() && offset.getY() <= level.getMaxBuildHeight()) {
                    var amount = IAuraChunk.getAuraInArea(level, offset, 14);
                    if (drain ? amount < bestAmount : amount > bestAmount) {
                        bestAmount = amount;
                        bestOffset = offset;
                    }
                }
            }
            if (bestOffset == null)
                break;

            var bestPos = drain ? IAuraChunk.getLowestSpot(level, bestOffset, 14, bestOffset)
                    : IAuraChunk.getHighestSpot(level, bestOffset, 14, bestOffset);
            var bestChunk = IAuraChunk.getAuraChunk(level, bestPos);

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
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return SpreadEffect.NAME;
    }
}
