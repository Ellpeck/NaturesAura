package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class TileEntityBlastFurnaceBooster extends TileEntityImpl implements ITickableTileEntity {

    public TileEntityBlastFurnaceBooster() {
        super(ModTileEntities.BLAST_FURNACE_BOOSTER);
    }

    @Override
    public void tick() {
        if (this.world.isRemote)
            return;

        TileEntity below = this.world.getTileEntity(this.pos.down());
        if (!(below instanceof BlastFurnaceTileEntity))
            return;
        BlastFurnaceTileEntity tile = (BlastFurnaceTileEntity) below;
        IRecipe<?> recipe = this.world.getRecipeManager().getRecipe(TileEntityFurnaceHeater.getRecipeType(tile), tile, this.world).orElse(null);
        if (recipe == null)
            return;

        IIntArray data = TileEntityFurnaceHeater.getFurnaceData(tile);
        int doneDiff = data.get(3) - data.get(2);
        if (doneDiff > 1)
            return;

        if (this.world.rand.nextFloat() > 0.45F) {
            PacketHandler.sendToAllAround(this.world, this.pos, 32,
                    new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 0));
            return;
        }

        ItemStack output = tile.getStackInSlot(2);
        if (output.getCount() >= output.getMaxStackSize())
            return;

        if (output.isEmpty()) {
            ItemStack result = recipe.getRecipeOutput();
            tile.setInventorySlotContents(2, result.copy());
        } else {
            output.grow(1);
        }

        BlockPos pos = IAuraChunk.getHighestSpot(this.world, this.pos, 30, this.pos);
        IAuraChunk.getAuraChunk(this.world, pos).drainAura(pos, 6500);

        PacketHandler.sendToAllAround(this.world, this.pos, 32,
                new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 1));
    }

    @Override
    public IItemHandlerModifiable getItemHandler(Direction facing) {
        TileEntity below = this.world.getTileEntity(this.pos.down());
        if (!(below instanceof BlastFurnaceTileEntity))
            return null;
        IItemHandler handler = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
        if (handler == null)
            return null;
        return new IItemHandlerModifiable() {
            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                if (handler instanceof IItemHandlerModifiable)
                    ((IItemHandlerModifiable) handler).setStackInSlot(0, stack);
            }

            @Override
            public int getSlots() {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return handler.getStackInSlot(0);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return handler.insertItem(0, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return handler.extractItem(0, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return handler.getSlotLimit(0);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return handler.isItemValid(0, stack);
            }
        };
    }

    @Override
    public void dropInventory() {
    }
}
