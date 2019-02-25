package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class TileEntityImpl extends TileEntity {

    public int redstonePower;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.writeNBT(compound, SaveType.TILE);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.readNBT(compound, SaveType.TILE);
    }

    public void writeNBT(NBTTagCompound compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            super.writeToNBT(compound);
            compound.setInteger("redstone", this.redstonePower);
        }
    }

    public void readNBT(NBTTagCompound compound, SaveType type) {
        if (type != SaveType.BLOCK) {
            super.readFromNBT(compound);
            this.redstonePower = compound.getInteger("redstone");
        }
    }

    public void onRedstonePowerChange() {

    }

    public void onRedstonePulse() {

    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound, SaveType.SYNC);
        return new SPacketUpdateTileEntity(this.pos, 0, compound);
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound, SaveType.SYNC);
        return compound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readNBT(tag, SaveType.SYNC);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readNBT(packet.getNbtCompound(), SaveType.SYNC);
    }

    public void sendToClients() {
        WorldServer world = (WorldServer) this.getWorld();
        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(this.getPos().getX() >> 4, this.getPos().getZ() >> 4);
        if (entry != null) {
            entry.sendPacket(this.getUpdatePacket());
        }
    }

    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        return null;
    }

    public IAuraContainer getAuraContainer(EnumFacing facing) {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.getItemHandler(facing) != null;
        } else if (capability == NaturesAuraAPI.capAuraContainer) {
            return this.getAuraContainer(facing) != null;
        } else {
            return super.hasCapability(capability, facing);
        }
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.getItemHandler(facing);
        } else if (capability == NaturesAuraAPI.capAuraContainer) {
            return (T) this.getAuraContainer(facing);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    public void dropInventory() {
        IItemHandler handler = this.getItemHandler(null);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    EntityItem item = new EntityItem(this.world,
                            this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                            stack);
                    this.world.spawnEntity(item);
                }
            }
        }
    }

    public ItemStack getDrop(IBlockState state, int fortune) {
        Block block = state.getBlock();
        ItemStack stack = new ItemStack(
                block.getItemDropped(state, this.world.rand, fortune),
                block.quantityDropped(state, fortune, this.world.rand),
                block.damageDropped(state));

        NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound, SaveType.BLOCK);

        if (!compound.isEmpty()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setTag("data", compound);
        }

        return stack;
    }

    public void loadDataOnPlace(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound().getCompoundTag("data");
            if (compound != null)
                this.readNBT(compound, SaveType.BLOCK);
        }
    }

    public boolean canGenerateRightNow(int range, int toAdd) {
        if (this.wantsLimitRemover()) {
            IBlockState below = this.world.getBlockState(this.pos.down());
            if (below.getBlock() == ModBlocks.GENERATOR_LIMIT_REMOVER)
                return true;
        }
        int aura = IAuraChunk.getAuraInArea(this.world, this.pos, range);
        return aura + toAdd <= IAuraChunk.DEFAULT_AURA * 2;
    }

    public boolean wantsLimitRemover() {
        return false;
    }

    public enum SaveType {
        TILE,
        SYNC,
        BLOCK
    }
}
