package de.ellpeck.naturesaura.api.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IVisualizable {

    @OnlyIn(Dist.CLIENT)
    AxisAlignedBB getVisualizationBounds(Level level, BlockPos pos);

    @OnlyIn(Dist.CLIENT)
    int getVisualizationColor(Level level, BlockPos pos);
}
