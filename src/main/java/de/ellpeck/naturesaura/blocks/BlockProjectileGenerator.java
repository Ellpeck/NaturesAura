package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityProjectileGenerator;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderProjectileGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

public class BlockProjectileGenerator extends BlockContainerImpl implements ITESRProvider<BlockEntityProjectileGenerator>, ICustomBlockState {

    public BlockProjectileGenerator() {
        super("projectile_generator", BlockEntityProjectileGenerator.class, Properties.of().strength(2.5F).sound(SoundType.STONE));

        NeoForge.EVENT_BUS.register(this);
        DispenserBlock.registerBehavior(Items.ENDER_PEARL, new AbstractProjectileDispenseBehavior() {

            @Override
            protected Projectile getProjectile(Level levelIn, Position position, ItemStack stackIn) {
                var ret = new ThrownEnderpearl(EntityType.ENDER_PEARL, levelIn);
                ret.setPos(position.x(), position.y(), position.z());
                return ret;
            }
        });
        DispenserBlock.registerBehavior(Items.TRIDENT, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level levelIn, Position position, ItemStack stackIn) {
                var ret = new ThrownTrident(EntityType.TRIDENT, levelIn);
                ret.setPos(position.x(), position.y(), position.z());
                ObfuscationReflectionHelper.setPrivateValue(ThrownTrident.class, ret, stackIn.copy(), "pickupItemStack");
                ret.pickup = AbstractArrow.Pickup.ALLOWED;
                return ret;
            }
        });
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide)
            return;
        var ray = event.getRayTraceResult();
        if (!(ray instanceof BlockHitResult blockRay))
            return;
        var pos = blockRay.getBlockPos();
        if (pos == null)
            return;
        var tile = entity.level().getBlockEntity(pos);
        if (!(tile instanceof BlockEntityProjectileGenerator generator))
            return;
        if (generator.nextSide != blockRay.getDirection())
            return;
        var amount = NaturesAuraAPI.PROJECTILE_GENERATIONS.get(entity.getType());
        if (amount == null || amount <= 0)
            return;
        if (!generator.canGenerateRightNow(amount))
            return;
        generator.generateAura(amount);

        PacketHandler.sendToAllAround(entity.level(), pos, 32,
                new PacketParticles((float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), PacketParticles.Type.PROJECTILE_GEN, pos.getX(), pos.getY(), pos.getZ()));
        entity.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.BLOCKS, 0.8F, 1F);

        generator.nextSide = generator.nextSide.getClockWise();
        generator.sendToClients();

        entity.kill();
        event.setCanceled(true);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModBlockEntities.PROJECTILE_GENERATOR, RenderProjectileGenerator::new);
    }
}
