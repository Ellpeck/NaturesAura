package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.level.Explosion;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.gen.Heightmap;

public class ExplosionEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "explosions");

    private float strength;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(level, pos, 85);
        if (aura > -5000000)
            return false;
        int chance = 140 - Math.abs(aura) / 200000;
        if (chance > 1 && level.rand.nextInt(chance) != 0)
            return false;
        this.strength = Math.min(Math.abs(aura) / 5000000F, 5F);
        if (this.strength <= 0)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 200000, 25, 100);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;

        int x = MathHelper.floor(pos.getX() + level.rand.nextGaussian() * this.dist);
        int z = MathHelper.floor(pos.getZ() + level.rand.nextGaussian() * this.dist);
        BlockPos chosenPos = new BlockPos(x, level.getHeight(Heightmap.Type.WORLD_SURFACE, x, z), z);
        if (chosenPos.distanceSq(pos) <= this.dist * this.dist && level.isBlockLoaded(chosenPos)) {
            level.createExplosion(null,
                    chosenPos.getX() + 0.5, chosenPos.getY() + 0.5, chosenPos.getZ() + 0.5,
                    this.strength, false, Explosion.Mode.DESTROY);
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.explosionEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
