package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;

public class ExplosionEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "explosions");

    private float strength;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        var aura = IAuraChunk.getAuraInArea(level, pos, 85);
        if (aura > -5000000)
            return false;
        var chance = 140 - Math.abs(aura) / 200000;
        if (chance > 1 && level.random.nextInt(chance) != 0)
            return false;
        this.strength = Math.min(Math.abs(aura) / 5000000F, 5F);
        if (this.strength <= 0)
            return false;
        this.dist = Mth.clamp(Math.abs(aura) / 200000, 25, 100);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level(), pos, spot))
            return ActiveType.INACTIVE;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;

        var x = Mth.floor(pos.getX() + level.random.nextGaussian() * this.dist);
        var z = Mth.floor(pos.getZ() + level.random.nextGaussian() * this.dist);
        var chosenPos = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
        if (chosenPos.distSqr(pos) <= this.dist * this.dist && level.isLoaded(chosenPos)) {
            var explosion = new Explosion(level, null, chosenPos.getX() + 0.5, chosenPos.getY() + 0.5, chosenPos.getZ() + 0.5, this.strength, false, Explosion.BlockInteraction.DESTROY);
            if (!EventHooks.onExplosionStart(level, explosion)) {
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.explosionEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return ExplosionEffect.NAME;
    }
}
