package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import net.minecraft.block.BlockFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TileEntityFurnaceHeater extends TileEntityImpl implements ITickable {

    public boolean isActive;

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 5 == 0) {
            boolean did = false;

            TileEntity tile = this.world.getTileEntity(this.pos.down());
            if (tile instanceof TileEntityFurnace) {
                TileEntityFurnace furnace = (TileEntityFurnace) tile;
                if (isReady(furnace)) {
                    int time = furnace.getField(0);
                    if (time <= 0)
                        BlockFurnace.setState(true, this.world, furnace.getPos());
                    furnace.setField(0, 200);
                    //if set higher than 199, it'll never finish because the furnace does ++ and then ==
                    furnace.setField(2, Math.min(199, furnace.getField(2) + 5));

                    BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 15, this.pos);
                    IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, spot);
                    chunk.drainAura(spot, MathHelper.ceil((200 - time) / 4F));
                    did = true;

                    if (this.world.getTotalWorldTime() % 15 == 0)
                        PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                this.pos.getX() + (float) this.world.rand.nextGaussian() * 5F,
                                this.pos.getY() + 1 + this.world.rand.nextFloat() * 5F,
                                this.pos.getZ() + (float) this.world.rand.nextGaussian() * 5F,
                                this.pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                                this.pos.getY() + 0.15F,
                                this.pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                                this.world.rand.nextFloat() * 0.07F + 0.07F, 0x89cc37, this.world.rand.nextFloat() + 0.5F
                        ));
                }
            }

            if (did != this.isActive) {
                this.isActive = did;
                this.sendToClients();
            }
        }
    }

    private static boolean isReady(TileEntityFurnace furnace) {
        if (!furnace.getStackInSlot(1).isEmpty())
            return false;

        ItemStack input = furnace.getStackInSlot(0);
        if (!input.isEmpty()) {
            ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
            if (output.isEmpty())
                return false;

            ItemStack currOutput = furnace.getStackInSlot(2);
            return currOutput.isEmpty() ||
                    Helper.areItemsEqual(currOutput, output, true) && currOutput.getCount() + output.getCount() <= output.getMaxStackSize();
        } else
            return false;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type == SaveType.SYNC)
            compound.setBoolean("active", this.isActive);
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);

        if (type == SaveType.SYNC)
            this.isActive = compound.getBoolean("active");
    }
}
