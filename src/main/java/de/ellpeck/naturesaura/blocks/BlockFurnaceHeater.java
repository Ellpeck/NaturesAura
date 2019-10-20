package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityFurnaceHeater;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFurnaceHeater extends BlockContainerImpl {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(2 / 16F, 0F, 2 / 16F, 14 / 16F, 4 / 16F, 14 / 16F);
    private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(2 / 16F, 12 / 16F, 2 / 16F, 14 / 16F, 1F, 14 / 16F);
    private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(2 / 16F, 2 / 16F, 12 / 16F, 14 / 16F, 14 / 16F, 1F);
    private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0F, 2 / 16F, 2 / 16F, 4 / 16F, 14 / 16F, 14 / 16F);
    private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(2 / 16F, 2 / 16F, 0F, 14 / 16F, 14 / 16F, 4 / 16F);
    private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(12 / 16F, 2 / 16F, 2 / 16F, 1F, 14 / 16F, 14 / 16F);

    public BlockFurnaceHeater() {
        super(Material.ROCK, "furnace_heater", TileEntityFurnaceHeater.class, "furnace_heater");
        this.setHardness(3F);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityFurnaceHeater && ((TileEntityFurnaceHeater) tile).isActive) {
            Direction facing = stateIn.getValue(FACING);

            float x;
            float y;
            float z;
            if (facing == Direction.UP) {
                x = 0.35F + rand.nextFloat() * 0.3F;
                y = 0F;
                z = 0.35F + rand.nextFloat() * 0.3F;
            } else if (facing == Direction.DOWN) {
                x = 0.35F + rand.nextFloat() * 0.3F;
                y = 1F;
                z = 0.35F + rand.nextFloat() * 0.3F;
            } else {
                x = facing.getZOffset() != 0 ? (0.35F + rand.nextFloat() * 0.3F) : facing.getXOffset() < 0 ? 1 : 0;
                y = 0.35F + rand.nextFloat() * 0.3F;
                z = facing.getXOffset() != 0 ? (0.35F + rand.nextFloat() * 0.3F) : facing.getZOffset() < 0 ? 1 : 0;
            }

            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getXOffset(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getYOffset(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getZOffset(),
                    0xf46e42, rand.nextFloat() + 0.5F, 55, 0F, true, true);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return AABB_DOWN;
            case NORTH:
                return AABB_NORTH;
            case EAST:
                return AABB_EAST;
            case SOUTH:
                return AABB_SOUTH;
            case WEST:
                return AABB_WEST;
            default:
                return AABB_UP;
        }
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
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(BlockState baseState, IBlockAccess world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, Direction.byIndex(meta));
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer, Hand hand) {
        return this.getDefaultState().withProperty(FACING, facing);
    }
}
