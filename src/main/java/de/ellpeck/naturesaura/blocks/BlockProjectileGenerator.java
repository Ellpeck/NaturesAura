package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderProjectileGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockProjectileGenerator extends BlockContainerImpl implements ITESRProvider {
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
        Integer amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(name);
        if (amount == null || amount <= 0)
            return;

        BlockPos spot = IAuraChunk.getLowestSpot(entity.world, pos, 35, pos);
        IAuraChunk.getAuraChunk(entity.world, spot).storeAura(spot, amount);

        PacketHandler.sendToAllAround(entity.world, pos, 32,
                new PacketParticles((float) entity.posX, (float) entity.posY, (float) entity.posZ, 26, pos.getX(), pos.getY(), pos.getZ()));
        entity.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.BLOCKS, 0.8F, 1F);

        generator.nextSide = generator.nextSide.rotateY();
        generator.sendToClients();

        entity.setDead();
        event.setCanceled(true);
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityProjectileGenerator.class, new RenderProjectileGenerator());
    }
}
