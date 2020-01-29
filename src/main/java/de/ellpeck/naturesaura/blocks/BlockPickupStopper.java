package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityPickupStopper;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockPickupStopper extends BlockContainerImpl implements IVisualizable, ICustomBlockState {
    public BlockPickupStopper() {
        super("pickup_stopper", TileEntityPickupStopper::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            ItemEntity item = event.getItem();
            BlockPos pos = item.getPosition();
            Helper.getTileEntitiesInArea(item.world, pos, 8, tile -> {
                if (!(tile instanceof TileEntityPickupStopper))
                    return false;
                TileEntityPickupStopper stopper = (TileEntityPickupStopper) tile;
                float radius = stopper.getRadius();
                if (radius <= 0F)
                    return false;
                BlockPos stopperPos = stopper.getPos();
                if (!new AxisAlignedBB(stopperPos).grow(radius).intersects(item.getBoundingBox()))
                    return false;

                event.setCanceled(true);

                if (item.world.getGameTime() % 3 == 0)
                    PacketHandler.sendToAllAround(item.world, pos, 32,
                            new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.PICKUP_STOPPER));
                return true;
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
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
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
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
