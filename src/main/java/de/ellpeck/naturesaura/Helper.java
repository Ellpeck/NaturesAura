package de.ellpeck.naturesaura;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

}
