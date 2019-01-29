package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ExplosionEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "explosions");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (spot >= 0 || world.getTotalWorldTime() % 40 != 0)
            return;
        int aura = IAuraChunk.getAuraInArea(world, pos, 85);
        if (aura > -5000000)
            return;
        int chance = 140 - Math.abs(aura) / 200000;
        if (chance > 1 && world.rand.nextInt(chance) != 0)
            return;
        float strength = Math.min(Math.abs(aura) / 5000000F, 5F);
        if (strength <= 0)
            return;
        int dist = MathHelper.clamp(Math.abs(aura) / 200000, 25, 100);

        int x = MathHelper.floor(pos.getX() + world.rand.nextGaussian() * dist);
        int z = MathHelper.floor(pos.getZ() + world.rand.nextGaussian() * dist);
        BlockPos chosenPos = new BlockPos(x, world.getHeight(x, z), z);
        if (chosenPos.distanceSq(pos) <= dist * dist && world.isBlockLoaded(chosenPos)) {
            world.newExplosion(null,
                    chosenPos.getX() + 0.5, chosenPos.getY() + 0.5, chosenPos.getZ() + 0.5,
                    strength, false, true);
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.explosionEffect;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
