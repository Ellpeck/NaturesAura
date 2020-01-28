package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderProjectileGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public class BlockProjectileGenerator extends BlockContainerImpl implements ITESRProvider {
    public BlockProjectileGenerator() {
        super("projectile_generator", TileEntityProjectileGenerator::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote)
            return;
        RayTraceResult ray = event.getRayTraceResult();
        if (!(ray instanceof BlockRayTraceResult))
            return;
        BlockRayTraceResult blockRay = (BlockRayTraceResult) ray;
        BlockPos pos = blockRay.getPos();
        if (pos == null)
            return;
        TileEntity tile = entity.world.getTileEntity(pos);
        if (!(tile instanceof TileEntityProjectileGenerator))
            return;
        TileEntityProjectileGenerator generator = (TileEntityProjectileGenerator) tile;
        if (generator.nextSide != blockRay.getFace())
            return;
        Integer amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(entity.getType());
        if (amount == null || amount <= 0)
            return;
        if (!generator.canGenerateRightNow(35, amount))
            return;

        BlockPos spot = IAuraChunk.getLowestSpot(entity.world, pos, 35, pos);
        IAuraChunk.getAuraChunk(entity.world, spot).storeAura(spot, amount);

        PacketHandler.sendToAllAround(entity.world, pos, 32,
                new PacketParticles((float) entity.getPosX(), (float) entity.getPosY(), (float) entity.getPosZ(), PacketParticles.Type.PROJECTILE_GEN, pos.getX(), pos.getY(), pos.getZ()));
        entity.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.BLOCKS, 0.8F, 1F);

        generator.nextSide = generator.nextSide.rotateY();
        generator.sendToClients();

        entity.remove();
        event.setCanceled(true);
    }

    @Override
    public Tuple<TileEntityType, Function<TileEntityRendererDispatcher, TileEntityRenderer<? extends TileEntity>>> getTESR() {
        return new Tuple<>(ModTileEntities.PROJECTILE_GENERATOR, RenderProjectileGenerator::new);
    }

}
