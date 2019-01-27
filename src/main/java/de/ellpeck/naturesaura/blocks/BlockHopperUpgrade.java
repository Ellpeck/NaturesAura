package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizableBlock;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityHopperUpgrade;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHopperUpgrade extends BlockContainerImpl implements IVisualizableBlock {
    public BlockHopperUpgrade() {
        super(Material.IRON, "hopper_upgrade", TileEntityHopperUpgrade.class, "hopper_upgrade");
        this.setSoundType(SoundType.METAL);
        this.setHardness(2.5F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(7);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x434f3f;
    }
}
