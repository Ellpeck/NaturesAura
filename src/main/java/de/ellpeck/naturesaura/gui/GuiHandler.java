package de.ellpeck.naturesaura.gui;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityEnderCrate;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(NaturesAura.MOD_ID, this);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 0) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileEntityEnderCrate) {
                TileEntityEnderCrate crate = (TileEntityEnderCrate) tile;
                if (crate.canOpen())
                    return new ContainerEnderCrate(player, crate.getItemHandler(null));
            }
        } else if (id == 1) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() == ModItems.ENDER_ACCESS && stack.hasDisplayName())
                return new ContainerEnderCrate(player, IWorldData.getOverworldData(world).getEnderStorage(stack.getDisplayName()));
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 0) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileEntityEnderCrate) {
                TileEntityEnderCrate crate = (TileEntityEnderCrate) tile;
                if (crate.canOpen())
                    return new GuiEnderCrate(player, crate.getItemHandler(null), "ender_crate", crate.name);
            }
        } else if (id == 1) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() == ModItems.ENDER_ACCESS && stack.hasDisplayName()) {
                String name = stack.getDisplayName();
                return new GuiEnderCrate(player, IWorldData.getOverworldData(world).getEnderStorage(name), "ender_access", name);
            }
        }
        return null;
    }
}
