package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.blocks.BlockSlabsNA;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ItemSlabNA extends BlockItem {

    private final Supplier<BlockSlabsNA> singleSlab;
    private final Supplier<BlockSlabsNA> doubleSlab;

    public ItemSlabNA(Block block, Supplier<BlockSlabsNA> singleSlab, Supplier<BlockSlabsNA> doubleSlab) {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            BlockState state = worldIn.getBlockState(pos);
            if (state.getBlock() == this.singleSlab.get()) {
                EnumBlockHalf half = state.getValue(SlabBlock.HALF);
                if (facing == Direction.UP && half == EnumBlockHalf.BOTTOM || facing == Direction.DOWN && half == EnumBlockHalf.TOP) {
                    BlockState newState = this.doubleSlab.get().getDefaultState();
                    AxisAlignedBB bound = newState.getCollisionBoundingBox(worldIn, pos);

                    if (bound != Block.NULL_AABB && worldIn.checkNoEntityCollision(bound.offset(pos)) && worldIn.setBlockState(pos, newState, 11)) {
                        SoundType sound = this.doubleSlab.get().getSoundType(newState, worldIn, pos, player);
                        worldIn.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                        stack.shrink(1);

                        if (player instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
                        }
                    }

                    return ActionResultType.SUCCESS;
                }
            }
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        } else {
            return ActionResultType.FAIL;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, Direction side, PlayerEntity player, ItemStack stack) {
        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == this.singleSlab.get())
            if (state.getValue(SlabBlock.HALF) == EnumBlockHalf.TOP ? side == Direction.DOWN : side == Direction.UP)
                return true;

        BlockState other = worldIn.getBlockState(pos.offset(side));
        return other.getBlock() == this.singleSlab.get() || super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }
}
