package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityRFConverter extends TileEntityImpl implements ITickable {

    public final RFStorage storage = new RFStorage();
    private int lastEnergy;

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        compound.setInteger("energy", this.storage.getEnergyStored());
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        this.storage.setEnergy(compound.getInteger("energy"));
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.lastEnergy != this.storage.getEnergyStored() && this.world.getTotalWorldTime() % 10 == 0) {
                this.sendToClients();
                this.lastEnergy = this.storage.getEnergyStored();
            }

            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
                if (tile == null || !tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite()))
                    continue;
                IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
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
            if (this.world.getTotalWorldTime() % 20 != 0)
                return;
            if (!Multiblocks.RF_CONVERTER.isComplete(this.world, this.pos))
                return;

            int aura = IAuraChunk.getAuraInArea(this.world, this.pos, 45);
            if (aura <= IAuraChunk.DEFAULT_AURA)
                return;
            int amountToGen = Math.min(Math.min(10000, aura / 1000), emptyPart);
            int amountToUse = MathHelper.ceil(amountToGen / ModConfig.general.auraToRFRatio);

            this.storage.setEnergy(this.storage.getEnergyStored() + amountToGen);
            BlockPos pos = IAuraChunk.getHighestSpot(this.world, this.pos, 45, this.pos);
            IAuraChunk.getAuraChunk(this.world, pos).drainAura(pos, amountToUse);

            PacketHandler.sendToAllAround(this.world, this.pos, 32,
                    new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 20));
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) this.storage;
        else
            return super.getCapability(capability, facing);
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
