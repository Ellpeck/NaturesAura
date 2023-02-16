package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockEntityChunkLoader extends BlockEntityImpl implements ITickableBlockEntity {

    private final List<ChunkPos> forcedChunks = new ArrayList<>();
    private boolean firstTick = true;
    private boolean canUseRightNow = true;

    public BlockEntityChunkLoader(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER, pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.loadChunks(true);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.level.isClientSide) {
            this.loadChunks(false);
            this.sendToClients();
        }
    }

    public int range() {
        return this.redstonePower * 2;
    }

    public int getAuraUsed() {
        return Mth.ceil(this.range() / 2F);
    }

    private void loadChunks(boolean unload) {
        if (this.level.isClientSide || !ModConfig.instance.chunkLoader.get())
            return;
        var level = (ServerLevel) this.level;

        List<ChunkPos> shouldBeForced = new ArrayList<>();
        if (!unload) {
            var range = this.range();
            if (range > 0 && this.canUseRightNow) {
                for (var x = this.worldPosition.getX() - range >> 4; x <= this.worldPosition.getX() + range >> 4; x++) {
                    for (var z = this.worldPosition.getZ() - range >> 4; z <= this.worldPosition.getZ() + range >> 4; z++) {
                        var pos = new ChunkPos(x, z);
                        // Only force chunks that we're already forcing or that nobody else is forcing
                        if (this.forcedChunks.contains(pos) || !level.getForcedChunks().contains(pos.toLong()))
                            shouldBeForced.add(pos);
                    }
                }
            }
        }

        // Unforce all the chunks that shouldn't be forced anymore
        for (var pos : this.forcedChunks) {
            if (!shouldBeForced.contains(pos))
                level.setChunkForced(pos.x, pos.z, false);
        }
        this.forcedChunks.clear();

        // Force all chunks that should be forced
        for (var pos : shouldBeForced) {
            level.setChunkForced(pos.x, pos.z, true);
            this.forcedChunks.add(pos);
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && ModConfig.instance.chunkLoader.get()) {
            // defer loading chunks on load to here since, otherwise, deadlocks happen oof
            // since forced chunks are saved to disk by the game, this is only necessary for when the chunk loader config changes
            if (this.firstTick) {
                this.loadChunks(false);
                this.firstTick = false;
            }

            var toUse = this.getAuraUsed();
            var canUse = this.canUseRightNow(toUse);
            if (this.canUseRightNow != canUse) {
                this.canUseRightNow = canUse;
                this.loadChunks(false);
            }

            if (this.level.getGameTime() % 20 != 0)
                return;
            if (toUse > 0 && this.canUseRightNow) {
                var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, toUse);
            }
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putLongArray("forced_chunks", this.forcedChunks.stream().map(ChunkPos::toLong).collect(Collectors.toList()));
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);

        if (type == SaveType.TILE) {
            this.forcedChunks.clear();
            Arrays.stream(compound.getLongArray("forced_chunks")).mapToObj(ChunkPos::new).forEach(this.forcedChunks::add);
        }
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }
}
