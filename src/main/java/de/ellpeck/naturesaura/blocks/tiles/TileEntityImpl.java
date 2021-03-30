package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TileEntityImpl extends TileEntity {

    public int redstonePower;
    private LazyOptional<IItemHandler> itemHandler;
    private LazyOptional<IAuraContainer> auraContainer;

    public TileEntityImpl(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        this.writeNBT(compound, SaveType.TILE);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        this.readNBT(compound, SaveType.TILE);
    }

    public void writeNBT(CompoundNBT compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            super.write(compound);
            compound.putInt("redstone", this.redstonePower);
        }
    }

    public void readNBT(CompoundNBT compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            // looks like the block state isn't used in the super
            super.read(null, compound);
            this.redstonePower = compound.getInt("redstone");
        }
    }

    public void onRedstonePowerChange(int newPower) {
        this.redstonePower = newPower;
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        this.writeNBT(compound, SaveType.SYNC);
        return new SUpdateTileEntityPacket(this.pos, 0, compound);
    }

    @Override
    public final CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();
        this.writeNBT(compound, SaveType.SYNC);
        return compound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.readNBT(tag, SaveType.SYNC);

    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        this.readNBT(packet.getNbtCompound(), SaveType.SYNC);
    }

    public void sendToClients() {
        ServerWorld world = (ServerWorld) this.getWorld();
        Stream<ServerPlayerEntity> entities = world.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.getPos()), false);
        SUpdateTileEntityPacket packet = this.getUpdatePacket();
        entities.forEach(e -> e.connection.sendPacket(packet));
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
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandler == null) {
                IItemHandler handler = this.getItemHandler();
                this.itemHandler = handler == null ? LazyOptional.empty() : LazyOptional.of(() -> handler);
            }
            return this.itemHandler.cast();
        } else if (capability == NaturesAuraAPI.capAuraContainer) {
            if (this.auraContainer == null) {
                IAuraContainer container = this.getAuraContainer();
                this.auraContainer = container == null ? LazyOptional.empty() : LazyOptional.of(() -> container);
            }
            return this.auraContainer.cast();
        } else {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (this.itemHandler != null)
            this.itemHandler.invalidate();
        if (this.auraContainer != null)
            this.auraContainer.invalidate();
    }

    public void dropInventory() {
        IItemHandler handler = this.getItemHandler();
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ItemEntity item = new ItemEntity(this.world,
                            this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                            stack);
                    this.world.addEntity(item);
                }
            }
        }
    }

    public void modifyDrop(ItemStack regularItem) {
        CompoundNBT compound = new CompoundNBT();
        this.writeNBT(compound, SaveType.BLOCK);
        if (!compound.isEmpty()) {
            if (!regularItem.hasTag())
                regularItem.setTag(new CompoundNBT());
            regularItem.getTag().put("data", compound);
        }
    }

    public void loadDataOnPlace(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag().getCompound("data");
            if (compound != null)
                this.readNBT(compound, SaveType.BLOCK);
        }
    }

    public boolean canGenerateRightNow(int toAdd) {
        if (this.wantsLimitRemover()) {
            BlockState below = this.world.getBlockState(this.pos.down());
            if (below.getBlock() == ModBlocks.GENERATOR_LIMIT_REMOVER)
                return true;
        }
        int aura = IAuraChunk.getAuraInArea(this.world, this.pos, 35);
        return aura + toAdd <= IAuraChunk.DEFAULT_AURA * 2;
    }

    public boolean wantsLimitRemover() {
        return false;
    }

    public void generateAura(int amount) {
        while (amount > 0) {
            BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 35, this.pos);
            amount -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, amount);
        }
    }

    public enum SaveType {
        TILE,
        SYNC,
        BLOCK
    }
}
