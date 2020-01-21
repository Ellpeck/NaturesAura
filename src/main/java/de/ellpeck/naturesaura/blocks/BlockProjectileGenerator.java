package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockProjectileGenerator extends BlockContainerImpl/* implements ITESRProvider*/ {
    public BlockProjectileGenerator() {
        super("projectile_generator", TileEntityProjectileGenerator.class, "projectile_generator", ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote)
            return;
        BlockRayTraceResult ray = (BlockRayTraceResult) event.getRayTraceResult();
        BlockPos pos = ray.getPos();
        if (pos == null)
            return;
        TileEntity tile = entity.world.getTileEntity(pos);
        if (!(tile instanceof TileEntityProjectileGenerator))
            return;
        TileEntityProjectileGenerator generator = (TileEntityProjectileGenerator) tile;
        if (generator.nextSide != ray.getFace())
            return;
        ResourceLocation name = ForgeRegistries.ENTITIES.getKey(entity.getType());
        Integer amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(name);
        if (amount == null || amount <= 0)
            return;

        BlockPos spot = IAuraChunk.getLowestSpot(entity.world, pos, 35, pos);
        IAuraChunk.getAuraChunk(entity.world, spot).storeAura(spot, amount);

        // TODO particles
       /* PacketHandler.sendToAllAround(entity.world, pos, 32,
                new PacketParticles((float) entity.posX, (float) entity.posY, (float) entity.posZ, 26, pos.getX(), pos.getY(), pos.getZ()));*/
        entity.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.BLOCKS, 0.8F, 1F);

        generator.nextSide = generator.nextSide.rotateY();
        generator.sendToClients();

        entity.remove();
        event.setCanceled(true);
    }

/*    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityProjectileGenerator.class, new RenderProjectileGenerator());
    }*/
}
