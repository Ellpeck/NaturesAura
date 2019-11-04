package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityFlowerGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFlowerGenerator extends BlockContainerImpl implements IVisualizable {

    public BlockFlowerGenerator() {
        super("flower_generator", TileEntityFlowerGenerator.class, "flower_generator", ModBlocks.prop(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2F));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(3);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xffed2b;
    }
}
