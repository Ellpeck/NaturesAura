package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.blocks.BlockSlabsNA;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class ItemSlabNA extends ItemBlock {

    private final Supplier<BlockSlabsNA> singleSlab;
    private final Supplier<BlockSlabsNA> doubleSlab;

    public ItemSlabNA(Block block, Supplier<BlockSlabsNA> singleSlab, Supplier<BlockSlabsNA> doubleSlab) {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            IBlockState state = worldIn.getBlockState(pos);
            if (state.getBlock() == this.singleSlab.get()) {
                EnumBlockHalf half = state.getValue(BlockSlab.HALF);
                if (facing == EnumFacing.UP && half == EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && half == EnumBlockHalf.TOP) {
                    IBlockState newState = this.doubleSlab.get().getDefaultState();
                    AxisAlignedBB bound = newState.getCollisionBoundingBox(worldIn, pos);

                    if (bound != Block.NULL_AABB && worldIn.checkNoEntityCollision(bound.offset(pos)) && worldIn.setBlockState(pos, newState, 11)) {
                        SoundType sound = this.doubleSlab.get().getSoundType(newState, worldIn, pos, player);
                        worldIn.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                        stack.shrink(1);

                        if (player instanceof EntityPlayerMP) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
                        }
                    }

                    return EnumActionResult.SUCCESS;
                }
            }
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == this.singleSlab.get())
            if (state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP ? side == EnumFacing.DOWN : side == EnumFacing.UP)
                return true;

        IBlockState other = worldIn.getBlockState(pos.offset(side));
        return other.getBlock() == this.singleSlab.get() || super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }
}
