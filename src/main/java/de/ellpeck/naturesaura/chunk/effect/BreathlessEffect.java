package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.potion.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BreathlessEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "breathless");

    private int amp;
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
        this.amp = Math.min(Mth.floor(Math.abs(aura) / 2500000F), 3);
        this.bb = new AABB(pos).inflate(dist);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getEyePosition()))
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.WHITE_WOOL);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 100 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.bb);
        for (LivingEntity entity : entities)
            entity.addEffect(new MobEffectInstance(ModPotions.BREATHLESS, 300, this.amp));
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.breathlessEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
