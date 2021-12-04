package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.BlockGratedChute;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

public class BlockEntityGratedChute extends BlockEntityImpl implements ITickableBlockEntity {

    public boolean isBlacklist;
    private final ItemStackHandlerNA items = new ItemStackHandlerNA(1, this, true) {
        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            return BlockEntityGratedChute.this.redstonePower <= 0;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return BlockEntityGratedChute.this.isBlacklist != BlockEntityGratedChute.this.isItemInFrame(stack);
        }
    };
    private int cooldown;

    public BlockEntityGratedChute() {
        super(ModTileEntities.GRATED_CHUTE);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.cooldown <= 0) {
                this.cooldown = 6;
                if (this.redstonePower > 0)
                    return;

                ItemStack curr = this.items.getStackInSlot(0);
                push:
                if (!curr.isEmpty()) {
                    BlockState state = this.level.getBlockState(this.worldPosition);
                    Direction facing = state.get(BlockGratedChute.FACING);
                    BlockEntity tile = this.level.getBlockEntity(this.worldPosition.offset(facing));
                    if (tile == null)
                        break push;
                    IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                            facing.getOpposite()).orElse(null);
                    if (handler == null)
                        break push;
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack theoreticalDrain = this.items.extractItem(0, 1, true);
                        if (!theoreticalDrain.isEmpty()) {
                            ItemStack left = handler.insertItem(i, theoreticalDrain, false);
                            if (left.isEmpty()) {
                                this.items.extractItem(0, 1, false);
                                break push;
                            }
                        }
                    }
                }
                pull:
                if (curr.isEmpty() || curr.getCount() < curr.getMaxStackSize()) {
                    List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(
                            this.worldPosition.getX(), this.worldPosition.getY() + 0.5, this.worldPosition.getZ(),
                            this.worldPosition.getX() + 1, this.worldPosition.getY() + 2, this.worldPosition.getZ() + 1));
                    for (ItemEntity item : items) {
                        if (!item.isAlive())
                            continue;
                        ItemStack stack = item.getItem();
                        if (stack.isEmpty())
                            continue;
                        ItemStack left = this.items.insertItem(0, stack, false);
                        if (!ItemStack.areItemStacksEqual(stack, left)) {
                            if (left.isEmpty())
                                item.remove();
                            else
                                item.setItem(left);
                            break pull;
                        }
                    }

                    BlockEntity tileUp = this.level.getBlockEntity(this.worldPosition.up());
                    if (tileUp == null)
                        break pull;
                    IItemHandler handlerUp = tileUp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
                    if (handlerUp == null)
                        break pull;
                    for (int i = 0; i < handlerUp.getSlots(); i++) {
                        ItemStack theoreticalDrain = handlerUp.extractItem(i, 1, true);
                        if (!theoreticalDrain.isEmpty()) {
                            ItemStack left = this.items.insertItem(0, theoreticalDrain, false);
                            if (left.isEmpty()) {
                                handlerUp.extractItem(i, 1, false);
                                break pull;
                            }
                        }
                    }
                }
            } else
                this.cooldown--;
        }
    }

    private boolean isItemInFrame(ItemStack stack) {
        List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
        if (frames.isEmpty())
            return false;
        for (ItemFrameEntity frame : frames) {
            ItemStack frameStack = frame.getDisplayedItem();
            if (Helper.areItemsEqual(stack, frameStack, true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.putInt("cooldown", this.cooldown);
            compound.put("items", this.items.serializeNBT());
            compound.putBoolean("blacklist", this.isBlacklist);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.cooldown = compound.getInt("cooldown");
            this.items.deserializeNBT(compound.getCompound("items"));
            this.isBlacklist = compound.getBoolean("blacklist");
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.items;
    }
}
