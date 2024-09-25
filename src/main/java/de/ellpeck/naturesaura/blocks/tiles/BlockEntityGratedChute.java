package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.BlockGratedChute;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;

public class BlockEntityGratedChute extends BlockEntityImpl implements ITickableBlockEntity {

    public boolean isBlacklist;
    public final ItemStackHandlerNA items = new ItemStackHandlerNA(1, this, true) {
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

    public BlockEntityGratedChute(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRATED_CHUTE, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.cooldown <= 0) {
                this.cooldown = 6;
                if (this.redstonePower > 0)
                    return;

                var curr = this.items.getStackInSlot(0);
                push:
                if (!curr.isEmpty()) {
                    var state = this.level.getBlockState(this.worldPosition);
                    var facing = state.getValue(BlockGratedChute.FACING);
                    var tile = this.level.getBlockEntity(this.worldPosition.relative(facing));
                    if (tile == null)
                        break push;
                    var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, facing.getOpposite());
                    if (handler == null)
                        break push;
                    for (var i = 0; i < handler.getSlots(); i++) {
                        var theoreticalDrain = this.items.extractItem(0, 1, true);
                        if (!theoreticalDrain.isEmpty()) {
                            var left = handler.insertItem(i, theoreticalDrain, false);
                            if (left.isEmpty()) {
                                this.items.extractItem(0, 1, false);
                                break push;
                            }
                        }
                    }
                }
                pull:
                if (curr.isEmpty() || curr.getCount() < curr.getMaxStackSize()) {
                    var items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(
                        this.worldPosition.getX(), this.worldPosition.getY() + 0.5, this.worldPosition.getZ(),
                        this.worldPosition.getX() + 1, this.worldPosition.getY() + 2, this.worldPosition.getZ() + 1));
                    for (var item : items) {
                        if (!item.isAlive())
                            continue;
                        var stack = item.getItem();
                        if (stack.isEmpty())
                            continue;
                        var left = this.items.insertItem(0, stack, false);
                        if (!ItemStack.matches(stack, left)) {
                            if (left.isEmpty()) {
                                item.kill();
                            } else {
                                item.setItem(left);
                            }
                            break pull;
                        }
                    }

                    var tileUp = this.level.getBlockEntity(this.worldPosition.above());
                    if (tileUp == null)
                        break pull;
                    var handlerUp = this.level.getCapability(Capabilities.ItemHandler.BLOCK, tileUp.getBlockPos(), tileUp.getBlockState(), tileUp, Direction.DOWN);
                    if (handlerUp == null)
                        break pull;
                    for (var i = 0; i < handlerUp.getSlots(); i++) {
                        var theoreticalDrain = handlerUp.extractItem(i, 1, true);
                        if (!theoreticalDrain.isEmpty()) {
                            var left = this.items.insertItem(0, theoreticalDrain, false);
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
        var frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
        if (frames.isEmpty())
            return false;
        for (var frame : frames) {
            var frameStack = frame.getItem();
            if (Helper.areItemsEqual(stack, frameStack, true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type != SaveType.BLOCK) {
            compound.putInt("cooldown", this.cooldown);
            compound.put("items", this.items.serializeNBT(registries));
            compound.putBoolean("blacklist", this.isBlacklist);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type != SaveType.BLOCK) {
            this.cooldown = compound.getInt("cooldown");
            this.items.deserializeNBT(registries, compound.getCompound("items"));
            this.isBlacklist = compound.getBoolean("blacklist");
        }
    }

}
