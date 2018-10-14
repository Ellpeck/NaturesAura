package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TileEntityImpl extends TileEntity {
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.writeNBT(compound, false);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.readNBT(compound, false);
    }

    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeToNBT(compound);
    }

    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readFromNBT(compound);
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound, true);
        return new SPacketUpdateTileEntity(this.pos, 0, compound);
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound, true);
        return compound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readNBT(tag, true);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readNBT(packet.getNbtCompound(), true);
    }

    public void sendToClients() {
        WorldServer world = (WorldServer) this.getWorld();
        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(this.getPos().getX() >> 4, this.getPos().getZ() >> 4);
        if (entry != null) {
            entry.sendPacket(this.getUpdatePacket());
        }
    }
}
