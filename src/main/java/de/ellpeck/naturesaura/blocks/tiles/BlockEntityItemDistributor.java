package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityItemDistributor extends BlockEntityImpl implements ITickableBlockEntity {

    private int cooldown;
    private Direction currentSide = Direction.NORTH;
    public boolean isRandomMode;

    public BlockEntityItemDistributor(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_DISTRIBUTOR, pos, state);
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

        var above = this.getHandler(Direction.UP);
        if (above == null)
            return;
        var dest = this.getNextSide();
        if (dest == null)
            return;
        for (var i = 0; i < above.getSlots(); i++) {
            var stack = above.extractItem(i, 1, true);
            if (stack.isEmpty())
                continue;
            for (var j = 0; j < dest.getSlots(); j++) {
                var remain = dest.insertItem(j, stack, false);
                if (!ItemStack.matches(remain, stack)) {
                    above.extractItem(i, 1, false);
                    this.cooldown = 3;
                    return;
                }
            }
        }
    }

    private IItemHandler getHandler(Direction direction) {
        var offset = this.worldPosition.relative(direction);
        var tile = this.level.getBlockEntity(offset);
        if (tile == null)
            return null;
        return this.level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, direction.getOpposite());
    }

    private IItemHandler getNextSide() {
        if (this.isRandomMode) {
            List<IItemHandler> handlers = new ArrayList<>();
            for (var i = 0; i < 4; i++) {
                var handler = this.getHandler(Direction.values()[i]);
                if (handler != null)
                    handlers.add(handler);
            }
            if (handlers.isEmpty())
                return null;
            return handlers.get(this.level.random.nextInt(handlers.size()));
        } else {
            for (var i = 0; i < 4; i++) {
                this.currentSide = this.currentSide.getClockWise();
                var handler = this.getHandler(this.currentSide);
                if (handler != null)
                    return handler;
            }
            return null;
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type == SaveType.TILE) {
            compound.putInt("cooldown", this.cooldown);
            compound.putInt("side", this.currentSide.ordinal());
        }
        if (type != SaveType.BLOCK)
            compound.putBoolean("random", this.isRandomMode);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type == SaveType.TILE) {
            this.cooldown = compound.getInt("cooldown");
            this.currentSide = Direction.values()[compound.getInt("side")];
        }
        if (type != SaveType.BLOCK)
            this.isRandomMode = compound.getBoolean("random");
    }

}
