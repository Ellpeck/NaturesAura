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

public class MigrationEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "migration");

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
        MutableInt highestSpot = highestChunk.getDrainSpot(highestPos);
        if (highestSpot.intValue() <= 0)
            return;

        int toTransfer = Math.min(25, highestSpot.intValue());
        highestChunk.drainAura(highestPos, toTransfer);
        auraChunk.storeAura(pos, toTransfer);
    }

    @Override
    public boolean appliesToType(IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
