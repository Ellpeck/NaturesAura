package de.ellpeck.naturesaura;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public static boolean containsItem(List<ItemStack> list, ItemStack item) {
        for (ItemStack stack : list) {
            if (stack.isItemEqual(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsItem(ItemStack[] array, ItemStack item) {
        for (ItemStack stack : array) {
            if (stack.isItemEqual(item)) {
                return true;
            }
        }
        return false;
    }
}
