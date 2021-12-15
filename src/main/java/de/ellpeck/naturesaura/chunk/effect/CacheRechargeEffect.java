package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AABB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CacheRechargeEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "cache_recharge");

    private int amount;
    private AABB bb;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot < 100000)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 20);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        int dist = Mth.clamp(aura / 3500, 3, 15);
        this.bb = new AABB(pos).grow(dist);
        this.amount = Mth.ceil(aura / 250F / auraAndSpots.getRight());
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getPositionVec()))
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModItems.AURA_CACHE);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(level, pos, spot))
            return;
        List<Player> players = level.getEntitiesWithinAABB(Player.class, this.bb);
        for (Player player : players) {
            if (NaturesAuraAPI.instance().isEffectPowderActive(level, player.getPosition(), NAME))
                continue;
            if (NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, true)) {
                NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, false);
                auraChunk.drainAura(pos, this.amount);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.cacheRechargeEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
