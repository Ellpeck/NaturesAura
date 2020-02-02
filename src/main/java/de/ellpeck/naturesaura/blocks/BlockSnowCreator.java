package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySnowCreator;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockSnowCreator extends BlockContainerImpl implements IVisualizable {
    public BlockSnowCreator() {
        super("snow_creator", TileEntitySnowCreator::new, ModBlocks.prop(Blocks.CRAFTING_TABLE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySnowCreator) {
            int radius = ((TileEntitySnowCreator) tile).getRange();
            if (radius > 0)
                return new AxisAlignedBB(pos).grow(radius);
        }
        return null;
    }

    @Override
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xdbe9ff;
    }
}
