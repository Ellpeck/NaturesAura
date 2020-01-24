package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;

public class ExplosionEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "explosions");

    private float strength;
    private int dist;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(world, pos, 85);
        if (aura > -5000000)
            return false;
        int chance = 140 - Math.abs(aura) / 200000;
        if (chance > 1 && world.rand.nextInt(chance) != 0)
            return false;
        this.strength = Math.min(Math.abs(aura) / 5000000F, 5F);
        if (this.strength <= 0)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 200000, 25, 100);
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
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
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (world.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(world, pos, spot))
            return;

        int x = MathHelper.floor(pos.getX() + world.rand.nextGaussian() * this.dist);
        int z = MathHelper.floor(pos.getZ() + world.rand.nextGaussian() * this.dist);
        BlockPos chosenPos = new BlockPos(x, world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z), z);
        if (chosenPos.distanceSq(pos) <= this.dist * this.dist && world.isBlockLoaded(chosenPos)) {
            world.createExplosion(null,
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
