package de.ellpeck.naturesaura.api.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IVisualizable {

    @OnlyIn(Dist.CLIENT)
    AxisAlignedBB getVisualizationBounds(World world, BlockPos pos);

    @OnlyIn(Dist.CLIENT)
    int getVisualizationColor(World world, BlockPos pos);
}
