package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class BalanceEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "balance");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        if (spot.intValue() >= 0)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 25);
        if (aura >= 0)
            return;
        int radius = Math.min(80, Math.abs(aura) / 50);
        if (radius <= 0)
            return;
        BlockPos highestPos = IAuraChunk.getHighestSpot(world, pos, radius, null);
        if (highestPos == null)
            return;
        IAuraChunk highestChunk = IAuraChunk.getAuraChunk(world, highestPos);
        int toTransfer = Math.min(25, highestChunk.getDrainSpot(highestPos).intValue());
        int stored = auraChunk.storeAura(pos, toTransfer);
        highestChunk.drainAura(highestPos, stored);
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
