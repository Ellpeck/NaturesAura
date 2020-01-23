package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.BlockFurnaceHeater;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class TileEntityFurnaceHeater extends TileEntityImpl implements ITickableTileEntity {

    private static final Field FURNACE_DATA_FIELD = ObfuscationReflectionHelper.findField(AbstractFurnaceTileEntity.class, "field_214013_b");
    public boolean isActive;

    public TileEntityFurnaceHeater() {
        super(ModTileEntities.FURNACE_HEATER);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 5 == 0) {
            boolean did = false;

            Direction facing = this.world.getBlockState(this.pos).get(BlockFurnaceHeater.FACING);
            BlockPos tilePos = this.pos.offset(facing.getOpposite());
            TileEntity tile = this.world.getTileEntity(tilePos);
            if (tile instanceof AbstractFurnaceTileEntity) {
                AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) tile;
                if (this.isReady(furnace)) {
                    IIntArray data;
                    try {
                        data = (IIntArray) FURNACE_DATA_FIELD.get(furnace);
                    } catch (IllegalAccessException e) {
                        NaturesAura.LOGGER.fatal("Couldn't reflect furnace field", e);
                        return;
                    }

                    int burnTime = data.get(0);
                    if (burnTime <= 0)
                        this.world.setBlockState(tilePos, this.world.getBlockState(tilePos).with(AbstractFurnaceBlock.LIT, true));

                    int totalCookTime = data.get(3);
                    data.set(0, totalCookTime);
                    //if set higher than 199, it'll never finish because the furnace does ++ and then ==
                    data.set(2, Math.min(totalCookTime - 1, data.get(2) + 5));

                    BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 20, this.pos);
                    IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, spot);
                    chunk.drainAura(spot, MathHelper.ceil((totalCookTime - burnTime) * 16.6F));
                    did = true;

                    if (this.world.getGameTime() % 15 == 0) {
                        PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                this.pos.getX() + (float) this.world.rand.nextGaussian() * 5F,
                                this.pos.getY() + 1 + this.world.rand.nextFloat() * 5F,
                                this.pos.getZ() + (float) this.world.rand.nextGaussian() * 5F,
                                tilePos.getX() + this.world.rand.nextFloat(),
                                tilePos.getY() + this.world.rand.nextFloat(),
                                tilePos.getZ() + this.world.rand.nextFloat(),
                                this.world.rand.nextFloat() * 0.07F + 0.07F, IAuraType.forWorld(this.world).getColor(), this.world.rand.nextFloat() + 0.5F
                        ));
                    }
                }
            }

            if (did != this.isActive) {
                this.isActive = did;
                this.sendToClients();
            }
        }
    }

    private boolean isReady(AbstractFurnaceTileEntity furnace) {
        if (!furnace.getStackInSlot(1).isEmpty())
            return false;

        ItemStack input = furnace.getStackInSlot(0);
        if (!input.isEmpty()) {
            AbstractCookingRecipe recipe = this.world.getRecipeManager().getRecipe(getRecipeType(furnace), furnace, this.world).orElse(null);
            if (recipe == null)
                return false;
            ItemStack output = recipe.getRecipeOutput();
            ItemStack currOutput = furnace.getStackInSlot(2);
            return currOutput.isEmpty() || Helper.areItemsEqual(currOutput, output, true) && currOutput.getCount() + output.getCount() <= output.getMaxStackSize();
        } else
            return false;
    }

    private static IRecipeType<? extends AbstractCookingRecipe> getRecipeType(AbstractFurnaceTileEntity furnace) {
        if (furnace instanceof BlastFurnaceTileEntity) {
            return IRecipeType.BLASTING;
        } else if (furnace instanceof SmokerTileEntity) {
            return IRecipeType.SMOKING;
        } else {
            return IRecipeType.SMELTING;
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type == SaveType.SYNC)
            compound.putBoolean("active", this.isActive);
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);

        if (type == SaveType.SYNC)
            this.isActive = compound.getBoolean("active");
    }
}
