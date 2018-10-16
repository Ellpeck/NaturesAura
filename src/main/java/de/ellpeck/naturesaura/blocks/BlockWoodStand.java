package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWoodStand extends BlockContainerImpl {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super(Material.WOOD, "wood_stand", TileEntityWoodStand.class, "wood_stand");
        this.setHardness(1.5F);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityWoodStand) {
            TileEntityWoodStand stand = (TileEntityWoodStand) tile;
            if (stand.stack.isEmpty()) {
                ItemStack handStack = playerIn.getHeldItem(hand);
                if (!handStack.isEmpty()) {
                    if (!worldIn.isRemote) {
                        ItemStack copy = handStack.copy();
                        copy.setCount(1);
                        stand.stack = copy;
                        handStack.shrink(1);
                        stand.sendToClients();
                    }
                    return true;
                }
            } else {
                if (!worldIn.isRemote) {
                    playerIn.addItemStackToInventory(stand.stack);
                    stand.stack = ItemStack.EMPTY;
                    stand.sendToClients();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityWoodStand) {
                TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                if (!stand.stack.isEmpty()) {
                    EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stand.stack);
                    worldIn.spawnEntity(item);
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
