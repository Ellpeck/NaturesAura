package de.ellpeck.naturesaura.aura.chunk.effect;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.aura.chunk.ISpotDrainable;
import de.ellpeck.naturesaura.aura.container.IAuraContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

public class ReplenishingEffect implements IDrainSpotEffect {
    @Override
    public void update(World world, Chunk chunk, AuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":ReplenishingEffect");
        int amount = spot.intValue();
        if (amount < 0) {
            List<ISpotDrainable> tiles = new ArrayList<>();
            Helper.getTileEntitiesInArea(world, pos, 25, tile -> {
                if (tile.hasCapability(Capabilities.auraContainer, null)) {
                    IAuraContainer container = tile.getCapability(Capabilities.auraContainer, null);
                    if (container instanceof ISpotDrainable) {
                        tiles.add((ISpotDrainable) container);
                    }
                }
            });
            if (!tiles.isEmpty()) {
                for (int i = world.rand.nextInt(6); i >= 0; i--) {
                    ISpotDrainable tile = tiles.get(world.rand.nextInt(tiles.size()));
                    int drained = tile.drainAuraPassively(-amount, false);
                    auraChunk.storeAura(pos, drained);
                    amount += drained;
                    if (amount >= drained) {
                        break;
                    }
                }
            }
        }
        world.profiler.endSection();
    }
}
