package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.BlockFurnaceHeater;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class BlockEntityFurnaceHeater extends BlockEntityImpl implements ITickableBlockEntity {

    private static final Field FURNACE_DATA_FIELD = ObfuscationReflectionHelper.findField(AbstractFurnaceBlockEntity.class, "dataAccess");
    public boolean isActive;

    public BlockEntityFurnaceHeater(BlockPos pos, BlockState state) {
        super(ModTileEntities.FURNACE_HEATER, pos, state);
    }

    public static ContainerData getFurnaceData(AbstractFurnaceBlockEntity tile) {
        try {
            return (ContainerData) FURNACE_DATA_FIELD.get(tile);
        } catch (IllegalAccessException e) {
            NaturesAura.LOGGER.fatal("Couldn't reflect furnace field", e);
            return null;
        }
    }

    public static RecipeType<? extends AbstractCookingRecipe> getRecipeType(AbstractFurnaceBlockEntity furnace) {
        if (furnace instanceof BlastFurnaceBlockEntity) {
            return RecipeType.BLASTING;
        } else if (furnace instanceof SmokerBlockEntity) {
            return RecipeType.SMOKING;
        } else {
            return RecipeType.SMELTING;
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 5 == 0) {
            boolean did = false;

            Direction facing = this.level.getBlockState(this.worldPosition).getValue(BlockFurnaceHeater.FACING);
            BlockPos tilePos = this.worldPosition.relative(facing.getOpposite());
            BlockEntity tile = this.level.getBlockEntity(tilePos);
            if (tile instanceof AbstractFurnaceBlockEntity furnace && this.isReady(furnace)) {
                ContainerData data = getFurnaceData(furnace);
                int burnTime = data.get(0);
                if (burnTime <= 0)
                    this.level.setBlockAndUpdate(tilePos, this.level.getBlockState(tilePos).setValue(AbstractFurnaceBlock.LIT, true));

                data.set(0, 200);
                //if set higher than 199, it'll never finish because the furnace does ++ and then ==
                data.set(2, Math.min(data.get(3) - 1, data.get(2) + 5));

                BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 20, this.worldPosition);
                IAuraChunk chunk = IAuraChunk.getAuraChunk(this.level, spot);
                chunk.drainAura(spot, Mth.ceil((200 - burnTime) * 16.6F));
                did = true;

                if (this.level.getGameTime() % 15 == 0) {
                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                            this.worldPosition.getX() + (float) this.level.random.nextGaussian() * 5F,
                            this.worldPosition.getY() + 1 + this.level.random.nextFloat() * 5F,
                            this.worldPosition.getZ() + (float) this.level.random.nextGaussian() * 5F,
                            tilePos.getX() + this.level.random.nextFloat(),
                            tilePos.getY() + this.level.random.nextFloat(),
                            tilePos.getZ() + this.level.random.nextFloat(),
                            this.level.random.nextFloat() * 0.07F + 0.07F, IAuraType.forLevel(this.level).getColor(), this.level.random.nextFloat() + 0.5F
                    ));
                }
            }

            if (did != this.isActive) {
                this.isActive = did;
                this.sendToClients();
            }
        }
    }

    private boolean isReady(AbstractFurnaceBlockEntity furnace) {
        if (!furnace.getItem(1).isEmpty())
            return false;

        ItemStack input = furnace.getItem(0);
        if (!input.isEmpty()) {
            AbstractCookingRecipe recipe = this.level.getRecipeManager().getRecipeFor(getRecipeType(furnace), furnace, this.level).orElse(null);
            if (recipe == null)
                return false;
            ItemStack output = recipe.getResultItem();
            ItemStack currOutput = furnace.getItem(2);
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
