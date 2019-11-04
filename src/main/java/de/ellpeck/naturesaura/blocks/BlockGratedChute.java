package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityGratedChute;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class BlockGratedChute extends BlockContainerImpl {

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

    private static VoxelShape BASE = VoxelShapes.combine()

    private static final VoxelShape SHAPES[] {

    }

    public BlockGratedChute() {
        super("grated_chute", TileEntityGratedChute.class, "grated_chute", ModBlocks.prop(Material.IRON).hardnessAndResistance(3.0F, 8.0F).sound(SoundType.METAL));
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
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
    }

    @Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        EnumFacing newFacing = facing.getOpposite();
        if (newFacing == EnumFacing.UP)
            newFacing = EnumFacing.DOWN;
        return this.getDefaultState().withProperty(FACING, newFacing);
    }

    @Override
    public boolean isTopSolid(BlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSideBeRendered(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
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
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }
}
