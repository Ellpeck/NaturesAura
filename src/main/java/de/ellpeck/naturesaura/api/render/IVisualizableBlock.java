package de.ellpeck.naturesaura.api.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IVisualizableBlock {

    @SideOnly(Side.CLIENT)
    AxisAlignedBB getVisualizationBounds(World world, BlockPos pos);

    @SideOnly(Side.CLIENT)
    int getVisualizationColor(World world, BlockPos pos);
}
