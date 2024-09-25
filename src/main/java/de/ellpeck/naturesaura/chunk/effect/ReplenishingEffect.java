package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.chunk.ISpotDrainable;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class ReplenishingEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "replenishing");

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot) {
        if (spot < 0) {
            List<ISpotDrainable> tiles = new ArrayList<>();
            Helper.getBlockEntitiesInArea(level, pos, 25, tile -> {
                var container = level.getCapability(NaturesAuraAPI.AURA_CONTAINER_BLOCK_CAPABILITY, tile.getBlockPos(), tile.getBlockState(), tile, null);
                if (container instanceof ISpotDrainable)
                    tiles.add((ISpotDrainable) container);
                return false;
            });
            if (!tiles.isEmpty()) {
                var type = IAuraType.forLevel(level);
                for (var i = level.random.nextInt(6); i >= 0; i--) {
                    var tile = tiles.get(level.random.nextInt(tiles.size()));
                    if (!tile.isAcceptableType(type))
                        continue;
                    var drained = tile.drainAuraPassively(-spot, false);
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
        return ReplenishingEffect.NAME;
    }

}
