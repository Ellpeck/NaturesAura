package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityAutoCrafter;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAutoCrafter extends BlockContainerImpl {
    public static final PropertyDirection FACING = HorizontalBlock.FACING;

    public BlockAutoCrafter() {
        super(Material.WOOD, "auto_crafter", TileEntityAutoCrafter.class, "auto_crafter");
        this.setSoundType(SoundType.WOOD);
        this.setHardness(1.5F);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, Direction.byHorizontalIndex(meta));
    }

    @Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }
}
