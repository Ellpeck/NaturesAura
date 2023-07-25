package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlockEntityOfferingTable extends BlockEntityImpl implements ITickableBlockEntity {

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 16;
        }
    };
    private final Queue<ItemStack> itemsToSpawn = new ArrayDeque<>();

    public BlockEntityOfferingTable(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OFFERING_TABLE, pos, state);
    }

    private OfferingRecipe getRecipe(ItemStack input) {
        for (var recipe : this.level.getRecipeManager().getRecipesFor(ModRecipes.OFFERING_TYPE, null, this.level))
            if (recipe.input.test(input))
                return recipe;
        return null;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 20 == 0) {
                if (!Multiblocks.OFFERING_TABLE.isComplete(this.level, this.worldPosition))
                    return;

                var stack = this.items.getStackInSlot(0);
                if (stack.isEmpty())
                    return;

                var items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.worldPosition).inflate(1));
                if (items.isEmpty())
                    return;

                var recipe = this.getRecipe(stack);
                if (recipe == null)
                    return;

                for (var item : items) {
                    if (!item.isAlive() || item.hasPickUpDelay())
                        continue;

                    var itemStack = item.getItem();
                    if (itemStack.isEmpty() || itemStack.getCount() != 1)
                        continue;

                    if (!recipe.startItem.test(itemStack))
                        continue;

                    var amount = Helper.getIngredientAmount(recipe.input);
                    var recipeCount = stack.getCount() / amount;
                    stack.shrink(recipeCount * amount);
                    item.kill();
                    this.sendToClients();

                    for (var i = 0; i < recipeCount; i++)
                        this.itemsToSpawn.add(recipe.output.copy());

                    if (Multiblocks.OFFERING_TABLE.forEach(this.worldPosition, 'R', (pos, m) -> this.level.getBlockState(pos).getBlock() == Blocks.WITHER_ROSE)) {
                        for (var i = this.level.random.nextInt(5) + 3; i >= 0; i--)
                            this.itemsToSpawn.add(new ItemStack(Items.BLACK_DYE));
                    }

                    var lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
                    lightningboltentity.setVisualOnly(true);
                    lightningboltentity.moveTo(Vec3.atCenterOf(this.worldPosition));
                    this.level.addFreshEntity(lightningboltentity);
                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                            (float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.OFFERING_TABLE,
                            this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()));

                    break;
                }
            } else if (this.level.getGameTime() % 3 == 0) {
                if (!this.itemsToSpawn.isEmpty())
                    this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5F, 256, this.worldPosition.getZ() + 0.5F, this.itemsToSpawn.remove()));
            }
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.items.serializeNBT());

            if (type != SaveType.SYNC) {
                var list = new ListTag();
                for (var stack : this.itemsToSpawn)
                    list.add(stack.serializeNBT());
                compound.put("items_to_spawn", list);
            }
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompound("items"));

            if (type != SaveType.SYNC) {
                this.itemsToSpawn.clear();
                var list = compound.getList("items_to_spawn", 10);
                for (var base : list)
                    this.itemsToSpawn.add(ItemStack.of((CompoundTag) base));
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.items;
    }

}
