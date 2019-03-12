package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockProjectileGenerator extends BlockContainerImpl {
    public BlockProjectileGenerator() {
        super(Material.ROCK, "projectile_generator", TileEntityProjectileGenerator.class, "projectile_generator");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2.5F);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote)
            return;
        RayTraceResult ray = event.getRayTraceResult();
        BlockPos pos = ray.getBlockPos();
        if (pos == null)
            return;
        TileEntity tile = entity.world.getTileEntity(pos);
        if (!(tile instanceof TileEntityProjectileGenerator))
            return;
        TileEntityProjectileGenerator generator = (TileEntityProjectileGenerator) tile;
        if (generator.nextSide != ray.sideHit)
            return;
        ResourceLocation name = EntityList.getKey(entity);
        int amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(name);
        if (amount <= 0)
            return;

        BlockPos spot = IAuraChunk.getLowestSpot(entity.world, pos, 35, pos);
        IAuraChunk.getAuraChunk(entity.world, spot).storeAura(spot, amount);

        generator.nextSide = generator.nextSide.rotateY();
        generator.sendToClients();

        entity.setDead();
        event.setCanceled(true);
    }
}
