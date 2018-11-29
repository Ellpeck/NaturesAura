package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.potion.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class BreathlessEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "breathless");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        if (spot.intValue() >= 0 || world.getTotalWorldTime() % 100 != 0)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 50);
        if (aura > 0)
            return;
        int dist = Math.min(Math.abs(aura) / 500, 75);
        if (dist < 10)
            return;
        int amp = Math.min(MathHelper.floor(Math.abs(aura) / 25000F), 3);

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(pos).grow(dist));
        for (EntityLivingBase entity : entities)
            entity.addPotionEffect(new PotionEffect(ModPotions.BREATHLESS, 300, amp));
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
