package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityItemDistributor extends BlockEntityImpl implements ITickableBlockEntity {

    private int cooldown;
    private Direction currentSide = Direction.NORTH;
    public boolean isRandomMode;

    public BlockEntityItemDistributor() {
        super(ModTileEntities.ITEM_DISTRIBUTOR);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        this.cooldown = 1;

        IItemHandler above = this.getHandler(Direction.UP);
        if (above == null)
            return;
        IItemHandler dest = this.getNextSide();
        if (dest == null)
            return;
        for (int i = 0; i < above.getSlots(); i++) {
            ItemStack stack = above.extractItem(i, 1, true);
            if (stack.isEmpty())
                continue;
            for (int j = 0; j < dest.getSlots(); j++) {
                ItemStack remain = dest.insertItem(j, stack, false);
                if (!ItemStack.areItemStacksEqual(remain, stack)) {
                    above.extractItem(i, 1, false);
                    this.cooldown = 3;
                    return;
                }
            }
        }
    }

    private IItemHandler getHandler(Direction direction) {
        BlockPos offset = this.worldPosition.offset(direction);
        BlockEntity tile = this.level.getBlockEntity(offset);
        if (tile == null)
            return null;
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);
    }

    private IItemHandler getNextSide() {
        if (this.isRandomMode) {
            List<IItemHandler> handlers = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                IItemHandler handler = this.getHandler(Direction.byHorizontalIndex(i));
                if (handler != null)
                    handlers.add(handler);
            }
            if (handlers.isEmpty())
                return null;
            return handlers.get(this.level.rand.nextInt(handlers.size()));
        } else {
            for (int i = 0; i < 4; i++) {
                this.currentSide = this.currentSide.rotateY();
                IItemHandler handler = this.getHandler(this.currentSide);
                if (handler != null)
                    return handler;
            }
            return null;
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE) {
            compound.putInt("cooldown", this.cooldown);
            compound.putInt("side", this.currentSide.ordinal());
        }
        if (type != SaveType.BLOCK)
            compound.putBoolean("random", this.isRandomMode);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE) {
            this.cooldown = compound.getInt("cooldown");
            this.currentSide = Direction.values()[compound.getInt("side")];
        }
        if (type != SaveType.BLOCK)
            this.isRandomMode = compound.getBoolean("random");
    }
}
