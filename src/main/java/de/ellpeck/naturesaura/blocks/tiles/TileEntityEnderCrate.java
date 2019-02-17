package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.misc.IWorldData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileEntityEnderCrate extends TileEntityImpl {

    public String name;

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        if (this.canOpen())
            return IWorldData.getOverworldData(this.world).getEnderStorage(this.name);
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
}
