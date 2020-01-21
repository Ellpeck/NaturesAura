package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

// TODO chunk loader
public class TileEntityChunkLoader extends TileEntityImpl implements ITickableTileEntity {
    public TileEntityChunkLoader(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    //private Ticket ticket;
/*
    @Override
    public void validate() {
        super.validate();
        if (!this.world.isRemote && this.ticket == null) {
            Ticket ticket = ForgeChunkManager.requestTicket(NaturesAura.instance, this.world, Type.NORMAL);
            this.updateTicket(ticket);
            ticket.getModData().setLong("pos", this.pos.toLong());
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.world.isRemote)
            this.updateTicket(null);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.world.isRemote) {
            this.loadChunks();
            this.sendToClients();
        }
    }*/

    public int range() {
        return this.redstonePower * 2;
    }

    /*public void updateTicket(Ticket ticket) {
        if (this.ticket != null)
            ForgeChunkManager.releaseTicket(this.ticket);
        this.ticket = ticket;
    }

    public void loadChunks() {
        if (this.ticket == null)
            return;
        Set<ChunkPos> before = new HashSet<>(this.ticket.getChunkList());
        int range = this.range();
        if (range > 0) {
            for (int x = (this.pos.getX() - range) >> 4; x <= (this.pos.getX() + range) >> 4; x++) {
                for (int z = (this.pos.getZ() - range) >> 4; z <= (this.pos.getZ() + range) >> 4; z++) {
                    ChunkPos pos = new ChunkPos(x, z);
                    if (!before.contains(pos))
                        ForgeChunkManager.forceChunk(this.ticket, pos);
                    else
                        before.remove(pos);
                }
            }
        }
        for (ChunkPos pos : before)
            ForgeChunkManager.unforceChunk(this.ticket, pos);
    }*/

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 20 != 0)
                return;
            int toUse = MathHelper.ceil(this.range() / 2F);
            if (toUse > 0) {
                BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
                IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, toUse);
            }
        }
    }
}
