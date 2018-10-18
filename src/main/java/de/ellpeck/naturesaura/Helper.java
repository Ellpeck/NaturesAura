package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class Helper {

    public static List<TileEntity> getTileEntitiesInArea(World world, BlockPos pos, int radius) {
        List<TileEntity> tiles = new ArrayList<>();
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                for (TileEntity tile : world.getChunk(x, z).getTileEntityMap().values()) {
                    if (tile.getPos().distanceSq(pos) <= radius * radius) {
                        tiles.add(tile);
                    }
                }
            }
        }
        return tiles;
    }

    public static boolean checkMultiblock(World world, BlockPos pos, BlockPos[] positions, IBlockState state, boolean blockOnly) {
        for (BlockPos offset : positions) {
            IBlockState current = world.getBlockState(pos.add(offset));
            if (blockOnly ? current.getBlock() != state.getBlock() : current != state) {
                return false;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static int blendColors(int c1, int c2, float ratio) {
        int a = (int) ((c1 >> 24 & 0xFF) * ratio + (c2 >> 24 & 0xFF) * (1 - ratio));
        int r = (int) ((c1 >> 16 & 0xFF) * ratio + (c2 >> 16 & 0xFF) * (1 - ratio));
        int g = (int) ((c1 >> 8 & 0xFF) * ratio + (c2 >> 8 & 0xFF) * (1 - ratio));
        int b = (int) ((c1 & 0xFF) * ratio + (c2 & 0xFF) * (1 - ratio));
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static int getItemIndex(List<ItemStack> list, ItemStack item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isItemEqual(item)) {
                return i;
            }
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemInWorld(ItemStack stack) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemInGui(ItemStack stack, int x, int y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stack, 0, 0, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static boolean putStackOnTile(EntityPlayer player, EnumHand hand, BlockPos pos, int slot) {
        TileEntity tile = player.world.getTileEntity(pos);
        if (tile instanceof TileEntityImpl) {
            IItemHandlerModifiable handler = ((TileEntityImpl) tile).getItemHandler(null);
            if (handler != null) {
                ItemStack handStack = player.getHeldItem(hand);
                if (!handStack.isEmpty()) {
                    ItemStack remain = handler.insertItem(slot, handStack, player.world.isRemote);
                    if (!ItemStack.areItemStacksEqual(remain, handStack)) {
                        if (!player.world.isRemote) {
                            player.setHeldItem(hand, remain);
                        }
                        return true;
                    }
                }

                if (!handler.getStackInSlot(slot).isEmpty()) {
                    if (!player.world.isRemote) {
                        player.addItemStackToInventory(handler.getStackInSlot(slot));
                        handler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
