package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizableBlock;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityPickupStopper;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPickupStopper extends BlockContainerImpl implements IVisualizableBlock {
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
                if (!new AxisAlignedBB(stopperPos).grow(radius).intersects(item.getEntityBoundingBox()))
                    return false;

                event.setCanceled(true);

                if (item.world.getTotalWorldTime() % 3 == 0)
                    PacketHandler.sendToAllAround(item.world, pos, 32,
                            new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 14));
                return true;
            });
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPickupStopper) {
            double radius = ((TileEntityPickupStopper) tile).getRadius();
            if (radius > 0)
                return new AxisAlignedBB(pos).grow(radius);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xf4aa42;
    }
}
