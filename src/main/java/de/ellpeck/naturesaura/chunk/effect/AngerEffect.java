package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class AngerEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "anger");

    private AxisAlignedBB bb;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(world, pos, 50);
        if (aura > 0)
            return false;
        int dist = Math.min(Math.abs(aura) / 50000, 75);
        if (dist < 10)
            return false;
        this.bb = new AxisAlignedBB(pos).grow(dist);
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
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
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (world.getGameTime() % 100 != 0)
            return;
        if (!this.calcValues(world, pos, spot))
            return;
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, this.bb);
        for (LivingEntity entity : entities) {
            if (!(entity instanceof IAngerable))
                continue;
            PlayerEntity player = world.getClosestPlayer(entity, 25);
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
