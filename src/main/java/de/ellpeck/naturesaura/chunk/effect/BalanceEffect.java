package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class BalanceEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "balance");

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 100000)
            return;
        if (level.getGameTime() % 200 != 0)
            return;
        int searchRadius = Math.min(45, spot / 10000);
        MutableInt positiveAura = new MutableInt();
        IAuraChunk.getSpotsInArea(level, pos, searchRadius, (otherPos, otherSpot) -> {
            if (otherSpot > 0)
                positiveAura.add(otherSpot);
        });
        int radius = Math.min(80, positiveAura.intValue() / 5000);
        BlockPos lowestPos = IAuraChunk.getLowestSpot(level, pos, radius, null);
        if (lowestPos == null)
            return;
        int stored = IAuraChunk.getAuraChunk(level, lowestPos).storeAura(lowestPos, spot / 50);
        auraChunk.drainAura(pos, stored);
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
