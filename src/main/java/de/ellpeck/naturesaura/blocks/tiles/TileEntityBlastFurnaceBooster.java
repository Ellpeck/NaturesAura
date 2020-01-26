package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;

public class TileEntityBlastFurnaceBooster extends TileEntityImpl implements ITickableTileEntity {

    private int waitTime;

    public TileEntityBlastFurnaceBooster() {
        super(ModTileEntities.BLAST_FURNACE_BOOSTER);
    }

    @Override
    public void tick() {
        if (this.world.isRemote)
            return;
        if (this.waitTime > 0) {
            this.waitTime--;
            return;
        }

        TileEntity below = this.world.getTileEntity(this.pos.down());
        if (!(below instanceof BlastFurnaceTileEntity))
            return;
        BlastFurnaceTileEntity tile = (BlastFurnaceTileEntity) below;
        IRecipe<?> recipe = this.world.getRecipeManager().getRecipe(TileEntityFurnaceHeater.getRecipeType(tile), tile, this.world).orElse(null);
        if (recipe == null)
            return;

        IIntArray data = TileEntityFurnaceHeater.getFurnaceData(tile);
        int doneDiff = data.get(3) - data.get(2);
        if (doneDiff > 1) {
            this.waitTime = doneDiff - 2;
            return;
        }

        if (this.world.rand.nextFloat() > 0.35F)
            return;

        ItemStack output = tile.getStackInSlot(2);
        if (output.getCount() >= output.getMaxStackSize())
            return;
        if (output.isEmpty()) {
            ItemStack result = recipe.getRecipeOutput();
            tile.setInventorySlotContents(2, result.copy());
        } else {
            output.grow(1);
        }
    }
}
