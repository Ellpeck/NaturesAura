package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class CacheRechargeEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "cache_recharge");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot < 1000)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 20);
        if (aura < 15000)
            return;
        if (NaturesAuraAPI.instance().isEffectPowderActive(world, pos, NAME))
            return;
        int dist = MathHelper.clamp(aura / 3500, 3, 15);
        int amount = aura / 2500 - 2;

        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(dist));
        for (EntityPlayer player : players) {
            if (NaturesAuraAPI.instance().insertAuraIntoPlayer(player, amount, true)) {
                NaturesAuraAPI.instance().insertAuraIntoPlayer(player, amount, false);
                auraChunk.drainAura(pos, amount);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.cacheRechargeEffect;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
