package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.render.IVisualizableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ItemRangeVisualizer extends ItemImpl {

    public static final Set<BlockPos> VISUALIZED_POSITIONS = new HashSet<>();

    public ItemRangeVisualizer() {
        super("range_visualizer");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizableBlock) {
            if (worldIn.isRemote)
                if (VISUALIZED_POSITIONS.contains(pos))
                    VISUALIZED_POSITIONS.remove(pos);
                else
                    VISUALIZED_POSITIONS.add(pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
