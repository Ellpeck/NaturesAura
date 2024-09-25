package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.capabilities.Capabilities;

public class BlockEntityImpl extends BlockEntity {

    public int redstonePower;

    public BlockEntityImpl(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        this.writeNBT(compound, SaveType.TILE, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.readNBT(tag, SaveType.TILE, registries);
    }

    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        if (type != SaveType.BLOCK) {
            super.saveAdditional(compound, registries);
            compound.putInt("redstone", this.redstonePower);
        }
    }

    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        if (type != SaveType.BLOCK) {
            super.loadWithComponents(compound, registries);
            this.redstonePower = compound.getInt("redstone");
        }
    }

    public void onRedstonePowerChange(int newPower) {
        this.redstonePower = newPower;
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (e, r) -> {
            var compound = new CompoundTag();
            this.writeNBT(compound, SaveType.SYNC, r);
            return compound;
        });
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var compound = new CompoundTag();
        this.writeNBT(compound, SaveType.SYNC, registries);
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.readNBT(tag, SaveType.SYNC, registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        this.readNBT(pkt.getTag(), SaveType.SYNC, registries);
    }

    public void sendToClients() {
        var world = (ServerLevel) this.getLevel();
        var entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.getBlockPos()), false);
        var packet = this.getUpdatePacket();
        for (var e : entities)
            e.connection.send(packet);
    }

    public void dropInventory() {
        var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.worldPosition, this.getBlockState(), this, null);
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
        this.writeNBT(compound, SaveType.BLOCK, this.getLevel().registryAccess());
        if (!compound.isEmpty()) {
            if (!regularItem.hasTag())
                regularItem.setTag(new CompoundTag());
            regularItem.getTag().put("data", compound);
        }
    }

    public void loadDataOnPlace(ItemStack stack) {
        if (stack.hasTag()) {
            var compound = stack.getTag().getCompound("data");
            if (compound != null)
                this.readNBT(compound, SaveType.BLOCK, this.level.registryAccess());
        }
    }

    public boolean canUseRightNow(int toUse) {
        if (this.allowsLowerLimiter()) {
            for (var dir : Direction.values()) {
                var offset = this.worldPosition.relative(dir);
                if (this.level.getBlockState(offset).getBlock() == ModBlocks.LOWER_LIMITER) {
                    var aura = IAuraChunk.getAuraInArea(this.level, this.worldPosition, 35);
                    return aura - toUse > 0;
                }
            }

        }
        return true;
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

    public boolean allowsLowerLimiter() {
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
