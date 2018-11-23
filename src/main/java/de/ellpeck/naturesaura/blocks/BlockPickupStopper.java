package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityPickupStopper;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockPickupStopper extends BlockContainerImpl {
    public BlockPickupStopper() {
        super(Material.ROCK, "pickup_stopper", TileEntityPickupStopper.class, "pickup_stopper");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2F);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player != null && !player.isSneaking()) {
            EntityItem item = event.getItem();
            BlockPos pos = item.getPosition();
            Helper.getTileEntitiesInArea(item.world, pos, 8, tile -> {
                if (!(tile instanceof TileEntityPickupStopper))
                    return false;
                TileEntityPickupStopper stopper = (TileEntityPickupStopper) tile;
                float radius = stopper.getRadius();
                if (radius <= 0F)
                    return false;
                BlockPos stopperPos = stopper.getPos();
                if (item.getDistanceSq(stopperPos.getX() + 0.5F, stopperPos.getY() + 0.5F, stopperPos.getZ() + 0.5F) > radius * radius)
                    return false;

                event.setCanceled(true);

                if (item.world.getTotalWorldTime() % 3 == 0)
                    PacketHandler.sendToAllAround(item.world, pos, 32,
                            new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 14));
                return true;
            });
        }
    }
}
