package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.chunk.ISpotDrainable;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class ReplenishingEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "replenishing");

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 0) {
            List<ISpotDrainable> tiles = new ArrayList<>();
            Helper.getBlockEntitiesInArea(level, pos, 25, tile -> {
                IAuraContainer container = tile.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null);
                if (container instanceof ISpotDrainable)
                    tiles.add((ISpotDrainable) container);
                return false;
            });
            if (!tiles.isEmpty()) {
                IAuraType type = IAuraType.forLevel(level);
                for (int i = level.random.nextInt(6); i >= 0; i--) {
                    ISpotDrainable tile = tiles.get(level.random.nextInt(tiles.size()));
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
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
