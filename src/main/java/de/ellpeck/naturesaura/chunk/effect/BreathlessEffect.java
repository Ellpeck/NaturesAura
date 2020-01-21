package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.potion.ModPotions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class BreathlessEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "breathless");

    private int amp;
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
        this.amp = Math.min(MathHelper.floor(Math.abs(aura) / 2500000F), 3);
        this.bb = new AxisAlignedBB(pos).grow(dist);
        return true;
    }

    @Override
    public int isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return -1;
        if (!this.bb.contains(player.getPositionVector()))
            return -1;
        return 1;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.WHITE_WOOL);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (world.getGameTime() % 100 != 0)
            return;
        if (!this.calcValues(world, pos, spot))
            return;
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, this.bb);
        for (LivingEntity entity : entities)
            entity.addPotionEffect(new EffectInstance(ModPotions.BREATHLESS, 300, this.amp));
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.breathlessEffect;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
