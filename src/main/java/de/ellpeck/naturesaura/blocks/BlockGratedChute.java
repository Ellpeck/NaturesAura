package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGratedChute;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

public class BlockGratedChute extends BlockContainerImpl {

    // TODO voxel shape stuff
    public static final DirectionProperty FACING = HopperBlock.FACING;
    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    private static final VoxelShape BASE_TOP = makeCuboidShape(0, 9, 0, 16, 10, 16);
    private static final VoxelShape BASE_SOUTH = makeCuboidShape(0, 9, 0, 16, 16, 1);
    private static final VoxelShape BASE_NORTH = makeCuboidShape(0, 9, 15, 16, 16, 16);
    private static final VoxelShape BASE_WEST = makeCuboidShape(15, 9, 0, 16, 26, 16);
    private static final VoxelShape BASE_EAST = makeCuboidShape(0, 9, 0, 1, 16, 16);
    private static final VoxelShape BASE_BOTTOM = makeCuboidShape(4, 4, 4, 12, 9, 12);

    public BlockGratedChute() {
        super("grated_chute", ModTileEntities.GRATED_CHUTE, ModBlocks.prop(Material.IRON).hardnessAndResistance(3.0F, 8.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!player.isSneaking())
            return false;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileEntityGratedChute))
            return false;
        if (!worldIn.isRemote) {
            TileEntityGratedChute chute = (TileEntityGratedChute) tile;
            chute.isBlacklist = !chute.isBlacklist;
            chute.sendToClients();
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand) {
        Direction newFacing = facing.getOpposite();
        if (newFacing == Direction.UP)
            newFacing = Direction.DOWN;
        return this.getDefaultState().with(FACING, newFacing);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityGratedChute) {
            IItemHandler handler = ((TileEntityGratedChute) tile).getItemHandler(null);
            ItemStack stack = handler.getStackInSlot(0);
            if (stack.isEmpty())
                return 0;
            return MathHelper.ceil((stack.getCount() / (float) stack.getMaxStackSize()) * 15);
        } else
            return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
