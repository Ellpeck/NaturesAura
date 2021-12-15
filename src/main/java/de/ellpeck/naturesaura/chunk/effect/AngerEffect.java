package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AABB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;

import java.util.List;

public class AngerEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "anger");

    private AABB bb;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(level, pos, 50);
        if (aura > 0)
            return false;
        int dist = Math.min(Math.abs(aura) / 50000, 75);
        if (dist < 10)
            return false;
        this.bb = new AABB(pos).grow(dist);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getPositionVec()))
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Items.FIRE_CHARGE);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 100 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        List<LivingEntity> entities = level.getEntitiesWithinAABB(LivingEntity.class, this.bb);
        for (LivingEntity entity : entities) {
            if (!(entity instanceof IAngerable))
                continue;
            Player player = level.getClosestPlayer(entity, 25);
            if (player == null)
                continue;
            ((IAngerable) entity).setAttackTarget(player);
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.angerEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
