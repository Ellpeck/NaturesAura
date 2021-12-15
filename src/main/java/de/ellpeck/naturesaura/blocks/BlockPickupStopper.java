package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityPickupStopper;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockPickupStopper extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    public BlockPickupStopper() {
        super("pickup_stopper", BlockEntityPickupStopper::new, Properties.of(Material.STONE).strength(2F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        var player = event.getPlayer();
        if (player != null && !player.isCrouching()) {
            var item = event.getItem();
            var pos = item.blockPosition();
            Helper.getBlockEntitiesInArea(item.level, pos, 8, tile -> {
                if (!(tile instanceof BlockEntityPickupStopper stopper))
                    return false;
                var radius = stopper.getRadius();
                if (radius <= 0F)
                    return false;
                var stopperPos = stopper.getBlockPos();
                if (!new AABB(stopperPos).inflate(radius).intersects(item.getBoundingBox()))
                    return false;

                event.setCanceled(true);

                if (item.level.getGameTime() % 3 == 0)
                    PacketHandler.sendToAllAround(item.level, pos, 32,
                            new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.PICKUP_STOPPER));
                return true;
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityPickupStopper) {
            double radius = ((BlockEntityPickupStopper) tile).getRadius();
            if (radius > 0)
                return new AABB(pos).inflate(radius);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0xf4aa42;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
