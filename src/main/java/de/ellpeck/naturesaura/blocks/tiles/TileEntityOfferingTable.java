package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class TileEntityOfferingTable extends TileEntityImpl implements ITickable {
    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true);
    private final Queue<ItemStack> itemsToSpawn = new ArrayDeque<>();

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 20 == 0) {
                if (!Multiblocks.OFFERING_TABLE.isComplete(this.world, this.pos))
                    return;

                ItemStack stack = this.items.getStackInSlot(0);
                if (stack.isEmpty())
                    return;

                List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.pos).grow(1));
                if (items.isEmpty())
                    return;

                OfferingRecipe recipe = getRecipe(stack);
                if (recipe == null)
                    return;

                for (EntityItem item : items) {
                    if (item.isDead || item.cannotPickup())
                        continue;

                    ItemStack itemStack = item.getItem();
                    if (itemStack.isEmpty() || itemStack.getCount() != 1)
                        continue;

                    if (!recipe.startItem.apply(itemStack))
                        continue;

                    int recipeCount = stack.getCount() / recipe.input.amount;
                    stack.shrink(recipeCount * recipe.input.amount);
                    item.setDead();
                    this.sendToClients();

                    for (int i = 0; i < recipeCount; i++)
                        this.itemsToSpawn.add(recipe.output.copy());

                    this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), true));
                    break;
                }
            } else if (this.world.getTotalWorldTime() % 3 == 0) {
                if (!this.itemsToSpawn.isEmpty())
                    this.world.spawnEntity(new EntityItem(
                            this.world,
                            this.pos.getX() + 0.5F + this.world.rand.nextGaussian() * 3F,
                            256,
                            this.pos.getZ() + 0.5F + this.world.rand.nextGaussian() * 3F,
                            this.itemsToSpawn.remove()));
            }
        }
    }

    private static OfferingRecipe getRecipe(ItemStack input) {
        for (OfferingRecipe recipe : NaturesAuraAPI.OFFERING_RECIPES.values())
            if (recipe.input.apply(input))
                return recipe;
        return null;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.setTag("items", this.items.serializeNBT());

            if (type != SaveType.SYNC) {
                NBTTagList list = new NBTTagList();
                for (ItemStack stack : this.itemsToSpawn) {
                    list.appendTag(stack.serializeNBT());
                }
                compound.setTag("items_to_spawn", list);
            }
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompoundTag("items"));

            if (type != SaveType.SYNC) {
                this.itemsToSpawn.clear();
                NBTTagList list = compound.getTagList("items_to_spawn", 10);
                for (NBTBase base : list) {
                    this.itemsToSpawn.add(new ItemStack((NBTTagCompound) base));
                }
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        return this.items;
    }
}
