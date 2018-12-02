package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.chunk.ISpotDrainable;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public class ReplenishingEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "replenishing");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 0) {
            List<ISpotDrainable> tiles = new ArrayList<>();
            Helper.getTileEntitiesInArea(world, pos, 25, tile -> {
                if (tile.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                    IAuraContainer container = tile.getCapability(NaturesAuraAPI.capAuraContainer, null);
                    if (container instanceof ISpotDrainable) {
                        tiles.add((ISpotDrainable) container);
                    }
                }
                return false;
            });
            if (!tiles.isEmpty()) {
                IAuraType type = IAuraType.forWorld(world);
                for (int i = world.rand.nextInt(6); i >= 0; i--) {
                    ISpotDrainable tile = tiles.get(world.rand.nextInt(tiles.size()));
                    if (!tile.isAcceptableType(type))
                        continue;
                    int drained = tile.drainAuraPassively(-spot, false);
                    if (drained <= 0)
                        continue;
                    auraChunk.storeAura(pos, drained);
                    spot += drained;
                    if (spot >= drained) {
                        break;
                    }
                }
            }
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
