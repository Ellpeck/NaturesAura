package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityProjectileGenerator;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderProjectileGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.tileentity.BlockEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockProjectileGenerator extends BlockContainerImpl implements ITESRProvider<BlockEntityProjectileGenerator>, ICustomBlockState {

    public BlockProjectileGenerator() {
        super("projectile_generator", BlockEntityProjectileGenerator::new, Properties.create(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
        DispenserBlock.registerDispenseBehavior(Items.ENDER_PEARL, new ProjectileDispenseBehavior() {
            @Override
            protected ProjectileEntity getProjectileEntity(Level levelIn, IPosition position, ItemStack stackIn) {
                EnderPearlEntity ret = new EnderPearlEntity(EntityType.ENDER_PEARL, levelIn);
                ret.setPosition(position.getX(), position.getY(), position.getZ());
                return ret;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.TRIDENT, new ProjectileDispenseBehavior() {
            @Override
            protected ProjectileEntity getProjectileEntity(Level levelIn, IPosition position, ItemStack stackIn) {
                TridentEntity ret = new TridentEntity(EntityType.TRIDENT, levelIn);
                ret.setPosition(position.getX(), position.getY(), position.getZ());
                // set thrownStack
                ObfuscationReflectionHelper.setPrivateValue(TridentEntity.class, ret, stackIn.copy(), "field_203054_h");
                ret.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
                return ret;
            }
        });
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (entity.level.isClientSide)
            return;
        RayTraceResult ray = event.getRayTraceResult();
        if (!(ray instanceof BlockRayTraceResult))
            return;
        BlockRayTraceResult blockRay = (BlockRayTraceResult) ray;
        BlockPos pos = blockRay.getPos();
        if (pos == null)
            return;
        BlockEntity tile = entity.level.getBlockEntity(pos);
        if (!(tile instanceof BlockEntityProjectileGenerator))
            return;
        BlockEntityProjectileGenerator generator = (BlockEntityProjectileGenerator) tile;
        if (generator.nextSide != blockRay.getFace())
            return;
        Integer amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(entity.getType());
        if (amount == null || amount <= 0)
            return;
        if (!generator.canGenerateRightNow(amount))
            return;
        generator.generateAura(amount);

        PacketHandler.sendToAllAround(entity.level, pos, 32,
                new PacketParticles((float) entity.getPosX(), (float) entity.getPosY(), (float) entity.getPosZ(), PacketParticles.Type.PROJECTILE_GEN, pos.getX(), pos.getY(), pos.getZ()));
        entity.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.BLOCKS, 0.8F, 1F);

        generator.nextSide = generator.nextSide.rotateY();
        generator.sendToClients();

        entity.remove();
        event.setCanceled(true);
    }

    @Override
    public Tuple<BlockEntityType<BlockEntityProjectileGenerator>, Supplier<Function<? super BlockEntityRendererDispatcher, ? extends BlockEntityRenderer<? super BlockEntityProjectileGenerator>>>> getTESR() {
        return new Tuple<>(ModTileEntities.PROJECTILE_GENERATOR, () -> RenderProjectileGenerator::new);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
