package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class BalanceEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "balance");

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot) {
        if (spot < 100000)
            return;
        if (level.getGameTime() % 200 != 0)
            return;
        var searchRadius = Math.min(45, spot / 10000);
        var positiveAura = new MutableInt();
        IAuraChunk.getSpotsInArea(level, pos, searchRadius, (otherPos, otherSpot) -> {
            if (otherSpot > 0)
                positiveAura.add(otherSpot);
        });
        var radius = Math.min(80, positiveAura.intValue() / 5000);
        var lowestPos = IAuraChunk.getLowestSpot(level, pos, radius, null);
        if (lowestPos == null)
            return;
        var stored = IAuraChunk.getAuraChunk(level, lowestPos).storeAura(lowestPos, spot / 50);
        auraChunk.drainAura(pos, stored);
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return BalanceEffect.NAME;
    }
}
