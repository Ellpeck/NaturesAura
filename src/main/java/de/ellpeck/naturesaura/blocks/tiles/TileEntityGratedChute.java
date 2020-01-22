package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.BlockGratedChute;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

public class TileEntityGratedChute extends TileEntityImpl implements ITickableTileEntity {

    private final ItemStackHandlerNA items = new ItemStackHandlerNA(1, this, true) {
        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            return TileEntityGratedChute.this.redstonePower <= 0;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return TileEntityGratedChute.this.isBlacklist != TileEntityGratedChute.this.isItemInFrame(stack);
        }
    };
    public boolean isBlacklist;
    private int cooldown;

    public TileEntityGratedChute() {
        super(ModTileEntities.GRATED_CHUTE);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.cooldown <= 0) {
                this.cooldown = 6;
                if (this.redstonePower > 0)
                    return;

                ItemStack curr = this.items.getStackInSlot(0);
                push:
                if (!curr.isEmpty()) {
                    BlockState state = this.world.getBlockState(this.pos);
                    Direction facing = state.get(BlockGratedChute.FACING);
                    TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
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
                    List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(
                            this.pos.getX(), this.pos.getY() + 0.5, this.pos.getZ(),
                            this.pos.getX() + 1, this.pos.getY() + 2, this.pos.getZ() + 1));
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

                    TileEntity tileUp = this.world.getTileEntity(this.pos.up());
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
        List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.world, this.pos);
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
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.putInt("cooldown", this.cooldown);
            compound.put("items", this.items.serializeNBT());
            compound.putBoolean("blacklist", this.isBlacklist);
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.cooldown = compound.getInt("cooldown");
            this.items.deserializeNBT(compound.getCompound("items"));
            this.isBlacklist = compound.getBoolean("blacklist");
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(Direction facing) {
        return this.items;
    }
}
