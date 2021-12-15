package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityPickupStopper;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.AABB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockPickupStopper extends BlockContainerImpl implements IVisualizable, ICustomBlockState {
    public BlockPickupStopper() {
        super("pickup_stopper", BlockEntityPickupStopper::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        Player player = event.getPlayer();
        if (player != null && !player.isSneaking()) {
            ItemEntity item = event.getItem();
            BlockPos pos = item.getPosition();
            Helper.getBlockEntitiesInArea(item.level, pos, 8, tile -> {
                if (!(tile instanceof BlockEntityPickupStopper))
                    return false;
                BlockEntityPickupStopper stopper = (BlockEntityPickupStopper) tile;
                float radius = stopper.getRadius();
                if (radius <= 0F)
                    return false;
                BlockPos stopperPos = stopper.getPos();
                if (!new AABB(stopperPos).grow(radius).intersects(item.getBoundingBox()))
                    return false;

                event.setCanceled(true);

                if (item.level.getGameTime() % 3 == 0)
                    PacketHandler.sendToAllAround(item.level, pos, 32,
                            new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.PICKUP_STOPPER));
                return true;
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityPickupStopper) {
            double radius = ((BlockEntityPickupStopper) tile).getRadius();
            if (radius > 0)
                return new AABB(pos).grow(radius);
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
