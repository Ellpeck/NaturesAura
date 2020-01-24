package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TileEntityChunkLoader extends TileEntityImpl implements ITickableTileEntity {

    private final List<ChunkPos> forcedChunks = new ArrayList<>();

    public TileEntityChunkLoader() {
        super(ModTileEntities.CHUNK_LOADER);
    }

    @Override
    public void validate() {
        super.validate();
        this.loadChunks(false);
    }

    @Override
    public void remove() {
        super.remove();
        this.loadChunks(true);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.world.isRemote) {
            this.loadChunks(false);
            this.sendToClients();
        }
    }

    public int range() {
        return this.redstonePower * 2;
    }

    private void loadChunks(boolean unload) {
        if (this.world.isRemote)
            return;
        ServerWorld world = (ServerWorld) this.world;

        List<ChunkPos> shouldBeForced = new ArrayList<>();
        if (!unload) {
            int range = this.range();
            if (range > 0) {
                for (int x = (this.pos.getX() - range) >> 4; x <= (this.pos.getX() + range) >> 4; x++) {
                    for (int z = (this.pos.getZ() - range) >> 4; z <= (this.pos.getZ() + range) >> 4; z++) {
                        ChunkPos pos = new ChunkPos(x, z);
                        // Only force chunks that we're already forcing or that nobody else is forcing
                        if (this.forcedChunks.contains(pos) || !world.getForcedChunks().contains(pos.asLong()))
                            shouldBeForced.add(pos);
                    }
                }
            }
        }

        // Unforce all of the chunks that shouldn't be forced anymore
        for (ChunkPos pos : this.forcedChunks) {
            if (!shouldBeForced.contains(pos))
                world.forceChunk(pos.x, pos.z, false);
        }
        this.forcedChunks.clear();

        // Force all chunks that should be forced
        for (ChunkPos pos : shouldBeForced) {
            world.forceChunk(pos.x, pos.z, true);
            this.forcedChunks.add(pos);
        }
    }

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

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putLongArray("forced_chunks", this.forcedChunks.stream().map(ChunkPos::asLong).collect(Collectors.toList()));
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);

        if (type == SaveType.TILE) {
            this.forcedChunks.clear();
            Arrays.stream(compound.getLongArray("forced_chunks")).mapToObj(ChunkPos::new).forEach(this.forcedChunks::add);
        }
    }
}
