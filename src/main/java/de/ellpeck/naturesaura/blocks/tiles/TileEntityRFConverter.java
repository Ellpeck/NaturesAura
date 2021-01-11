package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityRFConverter extends TileEntityImpl implements ITickableTileEntity {

    public final RFStorage storage = new RFStorage();
    private final LazyOptional<IEnergyStorage> storageOptional = LazyOptional.of(() -> this.storage);
    private int lastEnergy;

    public TileEntityRFConverter() {
        super(ModTileEntities.RF_CONVERTER);
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        compound.putInt("energy", this.storage.getEnergyStored());
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        this.storage.setEnergy(compound.getInt("energy"));
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && ModConfig.instance.rfConverter.get()) {
            if (this.lastEnergy != this.storage.getEnergyStored() && this.world.getGameTime() % 10 == 0) {
                this.sendToClients();
                this.lastEnergy = this.storage.getEnergyStored();
            }

            for (Direction facing : Direction.values()) {
                TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
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
            if (this.world.getGameTime() % 20 != 0)
                return;
            if (!Multiblocks.RF_CONVERTER.isComplete(this.world, this.pos))
                return;

            int aura = IAuraChunk.getAuraInArea(this.world, this.pos, 45);
            if (aura <= IAuraChunk.DEFAULT_AURA)
                return;
            int amountToGen = Math.min(Math.min(10000, aura / 1000), emptyPart);
            int amountToUse = MathHelper.ceil(amountToGen / ModConfig.instance.auraToRFRatio.get());

            this.storage.setEnergy(this.storage.getEnergyStored() + amountToGen);
            BlockPos pos = IAuraChunk.getHighestSpot(this.world, this.pos, 45, this.pos);
            IAuraChunk.getAuraChunk(this.world, pos).drainAura(pos, amountToUse);

            PacketHandler.sendToAllAround(this.world, this.pos, 32,
                    new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.RF_CONVERTER));
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
    public void remove() {
        super.remove();
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
