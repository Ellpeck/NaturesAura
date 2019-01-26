package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.BlockGratedChute;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

public class TileEntityGratedChute extends TileEntityImpl implements ITickable {

    private final ItemStackHandlerNA items = new ItemStackHandlerNA(1, this, false) {
        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            return TileEntityGratedChute.this.redstonePower <= 0;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return TileEntityGratedChute.this.isItemInFrame(stack);
        }
    };

    private int cooldown;

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.cooldown <= 0) {
                this.cooldown = 6;
                if (this.redstonePower > 0)
                    return;

                ItemStack curr = this.items.getStackInSlot(0);
                push:
                if (!curr.isEmpty()) {
                    IBlockState state = this.world.getBlockState(this.pos);
                    EnumFacing facing = state.getValue(BlockGratedChute.FACING);
                    TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
                    if (tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                            facing.getOpposite()))
                        break push;
                    IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                            facing.getOpposite());
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
                    List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(
                            this.pos.getX(), this.pos.getY() + 0.5, this.pos.getZ(),
                            this.pos.getX() + 1, this.pos.getY() + 2, this.pos.getZ() + 1));
                    for (EntityItem item : items) {
                        if (item.isDead)
                            continue;
                        ItemStack stack = item.getItem();
                        if (stack.isEmpty())
                            continue;
                        ItemStack left = this.items.insertItem(0, stack, false);
                        if (!ItemStack.areItemStacksEqual(stack, left)) {
                            if (left.isEmpty())
                                item.setDead();
                            else
                                item.setItem(left);
                            break pull;
                        }
                    }

                    TileEntity tileUp = this.world.getTileEntity(this.pos.up());
                    if (tileUp == null || !tileUp.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
                        break pull;
                    IItemHandler handlerUp = tileUp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
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
        List<EntityItemFrame> frames = Helper.getAttachedItemFrames(this.world, this.pos);
        if (frames.isEmpty())
            return true;
        for (EntityItemFrame frame : frames) {
            ItemStack frameStack = frame.getDisplayedItem();
            if (Helper.areItemsEqual(stack, frameStack, true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            compound.setInteger("cooldown", this.cooldown);
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.cooldown = compound.getInteger("cooldown");
    }

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        return this.items;
    }
}
