package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public class CacheRechargeEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "cache_recharge");

    private int amount;
    private AABB bb;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot < 100000)
            return false;
        var auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 20);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        var dist = Mth.clamp(aura / 3500, 3, 15);
        this.bb = new AABB(pos).inflate(dist);
        this.amount = Mth.ceil(aura / 250F / auraAndSpots.getRight());
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level(), pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getEyePosition()))
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.level(), player.blockPosition(), CacheRechargeEffect.NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModItems.AURA_CACHE);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot) {
        if (!this.calcValues(level, pos, spot))
            return;
        var players = level.getEntitiesOfClass(Player.class, this.bb);
        for (var player : players) {
            if (NaturesAuraAPI.instance().isEffectPowderActive(level, player.blockPosition(), CacheRechargeEffect.NAME))
                continue;
            if (NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, true)) {
                NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, false);
                auraChunk.drainAura(pos, this.amount);
            }
        }
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.cacheRechargeEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return CacheRechargeEffect.NAME;
    }
}
