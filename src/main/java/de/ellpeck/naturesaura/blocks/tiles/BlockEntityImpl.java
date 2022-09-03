package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class BlockEntityImpl extends BlockEntity {

    public int redstonePower;
    private LazyOptional<IItemHandler> itemHandler;
    private LazyOptional<IAuraContainer> auraContainer;

    public BlockEntityImpl(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        this.writeNBT(compound, SaveType.TILE);
    }

    @Override
    public void load(CompoundTag compound) {
        this.readNBT(compound, SaveType.TILE);
    }

    public void writeNBT(CompoundTag compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            super.saveAdditional(compound);
            compound.putInt("redstone", this.redstonePower);
        }
    }

    public void readNBT(CompoundTag compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            super.load(compound);
            this.redstonePower = compound.getInt("redstone");
        }
    }

    public void onRedstonePowerChange(int newPower) {
        this.redstonePower = newPower;
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, e -> {
            var compound = new CompoundTag();
            this.writeNBT(compound, SaveType.SYNC);
            return compound;
        });
    }

    @Override
    public final CompoundTag getUpdateTag() {
        var compound = new CompoundTag();
        this.writeNBT(compound, SaveType.SYNC);
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.readNBT(tag, SaveType.SYNC);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.readNBT(pkt.getTag(), SaveType.SYNC);
    }

    public void sendToClients() {
        var world = (ServerLevel) this.getLevel();
        var entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.getBlockPos()), false);
        var packet = this.getUpdatePacket();
        for (var e : entities)
            e.connection.send(packet);
    }

    public IItemHandlerModifiable getItemHandler() {
        return null;
    }

    public IAuraContainer getAuraContainer() {
        return null;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            if (this.itemHandler == null) {
                IItemHandler handler = this.getItemHandler();
                this.itemHandler = handler == null ? LazyOptional.empty() : LazyOptional.of(() -> handler);
            }
            return this.itemHandler.cast();
        } else if (capability == NaturesAuraAPI.CAP_AURA_CONTAINER) {
            if (this.auraContainer == null) {
                var container = this.getAuraContainer();
                this.auraContainer = container == null ? LazyOptional.empty() : LazyOptional.of(() -> container);
            }
            return this.auraContainer.cast();
        } else {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.itemHandler != null)
            this.itemHandler.invalidate();
        if (this.auraContainer != null)
            this.auraContainer.invalidate();
    }

    public void dropInventory() {
        IItemHandler handler = this.getItemHandler();
        if (handler != null) {
            for (var i = 0; i < handler.getSlots(); i++) {
                var stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    var item = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, stack);
                    this.level.addFreshEntity(item);
                }
            }
        }
    }

    public void modifyDrop(ItemStack regularItem) {
        var compound = new CompoundTag();
        this.writeNBT(compound, SaveType.BLOCK);
        if (!compound.isEmpty()) {
            if (!regularItem.hasTag()) regularItem.setTag(new CompoundTag());
            regularItem.getTag().put("data", compound);
        }
    }

    public void loadDataOnPlace(ItemStack stack) {
        if (stack.hasTag()) {
            var compound = stack.getTag().getCompound("data");
            if (compound != null) this.readNBT(compound, SaveType.BLOCK);
        }
    }

    public boolean canGenerateRightNow(int toAdd) {
        if (this.wantsLimitRemover()) {
            var below = this.level.getBlockState(this.worldPosition.below());
            if (below.getBlock() == ModBlocks.GENERATOR_LIMIT_REMOVER)
                return true;
        }
        var aura = IAuraChunk.getAuraInArea(this.level, this.worldPosition, 35);
        return aura + toAdd <= IAuraChunk.DEFAULT_AURA * 2;
    }

    public boolean wantsLimitRemover() {
        return false;
    }

    public void generateAura(int amount) {
        while (amount > 0) {
            var spot = IAuraChunk.getLowestSpot(this.level, this.worldPosition, 35, this.worldPosition);
            amount -= IAuraChunk.getAuraChunk(this.level, spot).storeAura(spot, amount);
        }
    }

    public enum SaveType {
        TILE, SYNC, BLOCK
    }
}
