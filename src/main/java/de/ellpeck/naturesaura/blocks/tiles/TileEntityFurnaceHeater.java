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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class BlockEntityFurnaceHeater extends BlockEntityImpl implements ITickableBlockEntity {

    private static final Field FURNACE_DATA_FIELD = ObfuscationReflectionHelper.findField(AbstractFurnaceBlockEntity.class, "field_214013_b");
    public boolean isActive;

    public BlockEntityFurnaceHeater() {
        super(ModTileEntities.FURNACE_HEATER);
    }

    public static IIntArray getFurnaceData(AbstractFurnaceBlockEntity tile) {
        try {
            return (IIntArray) FURNACE_DATA_FIELD.get(tile);
        } catch (IllegalAccessException e) {
            NaturesAura.LOGGER.fatal("Couldn't reflect furnace field", e);
            return null;
        }
    }

    public static IRecipeType<? extends AbstractCookingRecipe> getRecipeType(AbstractFurnaceBlockEntity furnace) {
        if (furnace instanceof BlastFurnaceBlockEntity) {
            return IRecipeType.BLASTING;
        } else if (furnace instanceof SmokerBlockEntity) {
            return IRecipeType.SMOKING;
        } else {
            return IRecipeType.SMELTING;
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 5 == 0) {
            boolean did = false;

            Direction facing = this.level.getBlockState(this.worldPosition).get(BlockFurnaceHeater.FACING);
            BlockPos tilePos = this.worldPosition.offset(facing.getOpposite());
            BlockEntity tile = this.level.getBlockEntity(tilePos);
            if (tile instanceof AbstractFurnaceBlockEntity) {
                AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) tile;
                if (this.isReady(furnace)) {
                    IIntArray data = getFurnaceData(furnace);
                    int burnTime = data.get(0);
                    if (burnTime <= 0)
                        this.level.setBlockState(tilePos, this.level.getBlockState(tilePos).with(AbstractFurnaceBlock.LIT, true));

                    data.set(0, 200);
                    //if set higher than 199, it'll never finish because the furnace does ++ and then ==
                    data.set(2, Math.min(data.get(3) - 1, data.get(2) + 5));

                    BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 20, this.worldPosition);
                    IAuraChunk chunk = IAuraChunk.getAuraChunk(this.level, spot);
                    chunk.drainAura(spot, MathHelper.ceil((200 - burnTime) * 16.6F));
                    did = true;

                    if (this.level.getGameTime() % 15 == 0) {
                        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                                this.worldPosition.getX() + (float) this.level.rand.nextGaussian() * 5F,
                                this.worldPosition.getY() + 1 + this.level.rand.nextFloat() * 5F,
                                this.worldPosition.getZ() + (float) this.level.rand.nextGaussian() * 5F,
                                tilePos.getX() + this.level.rand.nextFloat(),
                                tilePos.getY() + this.level.rand.nextFloat(),
                                tilePos.getZ() + this.level.rand.nextFloat(),
                                this.level.rand.nextFloat() * 0.07F + 0.07F, IAuraType.forLevel(this.level).getColor(), this.level.rand.nextFloat() + 0.5F
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

    private boolean isReady(AbstractFurnaceBlockEntity furnace) {
        if (!furnace.getStackInSlot(1).isEmpty())
            return false;

        ItemStack input = furnace.getStackInSlot(0);
        if (!input.isEmpty()) {
            AbstractCookingRecipe recipe = this.level.getRecipeManager().getRecipe(getRecipeType(furnace), furnace, this.level).orElse(null);
            if (recipe == null)
                return false;
            ItemStack output = recipe.getRecipeOutput();
            ItemStack currOutput = furnace.getStackInSlot(2);
            return currOutput.isEmpty() || Helper.areItemsEqual(currOutput, output, true) && currOutput.getCount() + output.getCount() <= output.getMaxStackSize();
        } else
            return false;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);

        if (type == SaveType.SYNC)
            compound.putBoolean("active", this.isActive);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);

        if (type == SaveType.SYNC)
            this.isActive = compound.getBoolean("active");
    }
}
