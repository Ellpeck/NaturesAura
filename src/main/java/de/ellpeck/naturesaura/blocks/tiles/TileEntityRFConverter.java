package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class BlockEntityRFConverter extends BlockEntityImpl implements ITickableBlockEntity {

    public final RFStorage storage = new RFStorage();
    private final LazyOptional<IEnergyStorage> storageOptional = LazyOptional.of(() -> this.storage);
    private int lastEnergy;

    public BlockEntityRFConverter(BlockPos pos, BlockState state) {
        super(ModTileEntities.RF_CONVERTER, pos, state);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        compound.putInt("energy", this.storage.getEnergyStored());
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        this.storage.setEnergy(compound.getInt("energy"));
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && ModConfig.instance.rfConverter.get()) {
            if (this.lastEnergy != this.storage.getEnergyStored() && this.level.getGameTime() % 10 == 0) {
                this.sendToClients();
                this.lastEnergy = this.storage.getEnergyStored();
            }

            for (Direction facing : Direction.values()) {
                BlockEntity tile = this.level.getBlockEntity(this.worldPosition.relative(facing));
                if (tile == null)
                    continue;
                IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).orElse(null);
                if (storage == null)
                    continue;
                int canStore = storage.receiveEnergy(Integer.MAX_VALUE, true);
                if (canStore <= 0)
                    continue;
                int extracted = this.storage.extractEnergy(canStore, false);
                if (extracted <= 0)
                    continue;
                storage.receiveEnergy(extracted, false);
                break;
            }

            int emptyPart = this.storage.getMaxEnergyStored() - this.storage.getEnergyStored();
            if (emptyPart <= 0)
                return;
            if (this.level.getGameTime() % 20 != 0)
                return;
            if (!Multiblocks.RF_CONVERTER.isComplete(this.level, this.worldPosition))
                return;

            int aura = IAuraChunk.getAuraInArea(this.level, this.worldPosition, 45);
            if (aura <= IAuraChunk.DEFAULT_AURA)
                return;
            int amountToGen = Math.min(Math.min(10000, aura / 1000), emptyPart);
            int amountToUse = Mth.ceil(amountToGen / ModConfig.instance.auraToRFRatio.get());

            this.storage.setEnergy(this.storage.getEnergyStored() + amountToGen);
            BlockPos pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 45, this.worldPosition);
            IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, amountToUse);

            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                    new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.RF_CONVERTER));
        }
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return this.storageOptional.cast();
        else
            return super.getCapability(capability, facing);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.storageOptional.invalidate();
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
