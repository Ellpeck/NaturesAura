package de.ellpeck.naturesaura.api.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IVisualizable {

    @OnlyIn(Dist.CLIENT)
    AABB getVisualizationBounds(Level level, BlockPos pos);

    @OnlyIn(Dist.CLIENT)
    int getVisualizationColor(Level level, BlockPos pos);
}
