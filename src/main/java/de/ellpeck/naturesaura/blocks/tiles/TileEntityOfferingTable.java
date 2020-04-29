package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class TileEntityOfferingTable extends TileEntityImpl implements ITickableTileEntity {
    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 16;
        }
    };
    private final Queue<ItemStack> itemsToSpawn = new ArrayDeque<>();

    public TileEntityOfferingTable() {
        super(ModTileEntities.OFFERING_TABLE);
    }

    private OfferingRecipe getRecipe(ItemStack input) {
        for (OfferingRecipe recipe : this.world.getRecipeManager().getRecipes(ModRecipes.OFFERING_TYPE, null, null))
            if (recipe.input.test(input))
                return recipe;
        return null;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 20 == 0) {
                if (!Multiblocks.OFFERING_TABLE.isComplete(this.world, this.pos))
                    return;

                ItemStack stack = this.items.getStackInSlot(0);
                if (stack.isEmpty())
                    return;

                List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(this.pos).grow(1));
                if (items.isEmpty())
                    return;

                OfferingRecipe recipe = this.getRecipe(stack);
                if (recipe == null)
                    return;

                for (ItemEntity item : items) {
                    if (!item.isAlive() || item.cannotPickup())
                        continue;

                    ItemStack itemStack = item.getItem();
                    if (itemStack.isEmpty() || itemStack.getCount() != 1)
                        continue;

                    if (!recipe.startItem.test(itemStack))
                        continue;

                    int amount = Helper.getIngredientAmount(recipe.input);
                    int recipeCount = stack.getCount() / amount;
                    stack.shrink(recipeCount * amount);
                    item.remove();
                    this.sendToClients();

                    for (int i = 0; i < recipeCount; i++)
                        this.itemsToSpawn.add(recipe.output.copy());

                    ((ServerWorld) this.world).addLightningBolt(new LightningBoltEntity(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), true));
                    PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(
                            (float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.OFFERING_TABLE,
                            this.pos.getX(), this.pos.getY(), this.pos.getZ()));

                    break;
                }
            } else if (this.world.getGameTime() % 3 == 0) {
                if (!this.itemsToSpawn.isEmpty())
                    this.world.addEntity(new ItemEntity(
                            this.world,
                            this.pos.getX() + 0.5F, 256, this.pos.getZ() + 0.5F,
                            this.itemsToSpawn.remove()));
            }
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.items.serializeNBT());

            if (type != SaveType.SYNC) {
                ListNBT list = new ListNBT();
                for (ItemStack stack : this.itemsToSpawn) {
                    list.add(stack.serializeNBT());
                }
                compound.put("items_to_spawn", list);
            }
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompound("items"));

            if (type != SaveType.SYNC) {
                this.itemsToSpawn.clear();
                ListNBT list = compound.getList("items_to_spawn", 10);
                for (INBT base : list) {
                    this.itemsToSpawn.add(ItemStack.read((CompoundNBT) base));
                }
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(Direction facing) {
        return this.items;
    }
}
