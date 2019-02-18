package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class TileEntityEnderCrate extends TileEntityImpl {

    private final IItemHandlerModifiable wrappedEnderStorage = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            this.getStorage().setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return this.getStorage().getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.getStorage().getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemStack remain = this.getStorage().insertItem(slot, stack, simulate);
            if (!simulate)
                TileEntityEnderCrate.this.drainAura((stack.getCount() - remain.getCount()) * 200);
            return remain;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack extracted = this.getStorage().extractItem(slot, amount, simulate);
            if (!simulate)
                TileEntityEnderCrate.this.drainAura(extracted.getCount() * 200);
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.getStorage().getSlotLimit(slot);
        }

        private IItemHandlerModifiable getStorage() {
            return IWorldData.getOverworldData(TileEntityEnderCrate.this.world).getEnderStorage(TileEntityEnderCrate.this.name);
        }
    };
    public String name;

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        if (this.canOpen())
            return this.wrappedEnderStorage;
        return null;
    }

    public boolean canOpen() {
        return this.name != null;
    }

    @Override
    public void dropInventory() {
    }

    @Override
    public ItemStack getDrop(IBlockState state, int fortune) {
        ItemStack drop = super.getDrop(state, fortune);
        if (this.name != null)
            drop.setStackDisplayName(this.name);
        return drop;
    }

    @Override
    public void loadDataOnPlace(ItemStack stack) {
        super.loadDataOnPlace(stack);
        if (!this.world.isRemote && stack.hasDisplayName())
            this.name = stack.getDisplayName();
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (this.name != null)
                compound.setString("name", this.name);
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (compound.hasKey("name"))
                this.name = compound.getString("name");
        }
    }

    public void drainAura(int amount) {
        if (amount > 0) {
            BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
            IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, amount);
        }
    }
}
