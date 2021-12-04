package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityHopperUpgrade;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockHopperUpgrade extends BlockContainerImpl implements IVisualizable {
    public BlockHopperUpgrade() {
        super("hopper_upgrade", BlockEntityHopperUpgrade::new, Properties.create(Material.IRON).hardnessAndResistance(2.5F).sound(SoundType.METAL));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(Level level, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(7);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0x434f3f;
    }
}
