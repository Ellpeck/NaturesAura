package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BalanceEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "balance");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 1000)
            return;
        int radius = Math.min(80, spot / 40);
        if (radius <= 0)
            return;
        BlockPos lowestPos = IAuraChunk.getLowestSpot(world, pos, radius, null);
        if (lowestPos == null)
            return;
        IAuraChunk lowestChunk = IAuraChunk.getAuraChunk(world, lowestPos);
        int toTransfer = Math.min(spot / 10, -lowestChunk.getDrainSpot(lowestPos));
        int stored = auraChunk.drainAura(pos, toTransfer);
        lowestChunk.storeAura(lowestPos, stored);
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
