package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;

public class BlockEntityRFConverter extends BlockEntityImpl implements ITickableBlockEntity {

    public final RFStorage storage = new RFStorage();
    private int lastEnergy;

    public BlockEntityRFConverter(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RF_CONVERTER, pos, state);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        compound.putInt("energy", this.storage.getEnergyStored());
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        this.storage.setEnergy(compound.getInt("energy"));
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && ModConfig.instance.rfConverter.get()) {
            if (this.lastEnergy != this.storage.getEnergyStored() && this.level.getGameTime() % 10 == 0) {
                this.sendToClients();
                this.lastEnergy = this.storage.getEnergyStored();
            }

            for (var facing : Direction.values()) {
                var tile = this.level.getBlockEntity(this.worldPosition.relative(facing));
                if (tile == null)
                    continue;
                var storage = this.level.getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, facing.getOpposite());
                if (storage == null)
                    continue;
                var canStore = storage.receiveEnergy(Integer.MAX_VALUE, true);
                if (canStore <= 0)
                    continue;
                var extracted = this.storage.extractEnergy(canStore, false);
                if (extracted <= 0)
                    continue;
                storage.receiveEnergy(extracted, false);
                break;
            }

            var emptyPart = this.storage.getMaxEnergyStored() - this.storage.getEnergyStored();
            if (emptyPart <= 0)
                return;
            if (this.level.getGameTime() % 20 != 0)
                return;
            if (!Multiblocks.RF_CONVERTER.isComplete(this.level, this.worldPosition))
                return;

            var aura = IAuraChunk.getAuraInArea(this.level, this.worldPosition, 45);
            if (aura <= IAuraChunk.DEFAULT_AURA)
                return;
            var amountToGen = Math.min(Math.min(10000, aura / 1000), emptyPart);
            var amountToUse = Mth.ceil(amountToGen / ModConfig.instance.auraToRFRatio.get());

            this.storage.setEnergy(this.storage.getEnergyStored() + amountToGen);
            var pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 45, this.worldPosition);
            IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, amountToUse);

            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                    new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.RF_CONVERTER));
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    public static class RFStorage extends EnergyStorage {

        public RFStorage() {
            super(50000, 0, 2000);
        }

        public void setEnergy(int energy) {
            this.energy = energy;
        }

    }

}
