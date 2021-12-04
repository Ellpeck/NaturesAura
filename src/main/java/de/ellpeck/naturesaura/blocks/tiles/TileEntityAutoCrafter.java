package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.BlockAutoCrafter;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlockEntityAutoCrafter extends BlockEntityImpl implements ITickableBlockEntity {
    public final CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
        @Override
        public boolean canInteractWith(Player playerIn) {
            return false;
        }
    }, 3, 3);

    public BlockEntityAutoCrafter() {
        super(ModTileEntities.AUTO_CRAFTER);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 60 != 0)
                return;
            if (!Multiblocks.AUTO_CRAFTER.isComplete(this.level, this.worldPosition))
                return;
            this.crafting.clear();

            BlockState state = this.level.getBlockState(this.worldPosition);
            Direction facing = state.get(BlockAutoCrafter.FACING);
            BlockPos middlePos = this.worldPosition.up();
            BlockPos topPos = middlePos.offset(facing, 2);
            BlockPos bottomPos = middlePos.offset(facing.getOpposite(), 2);
            BlockPos[] poses = new BlockPos[]{
                    topPos.offset(facing.rotateYCCW(), 2),
                    topPos,
                    topPos.offset(facing.rotateY(), 2),
                    middlePos.offset(facing.rotateYCCW(), 2),
                    middlePos,
                    middlePos.offset(facing.rotateY(), 2),
                    bottomPos.offset(facing.rotateYCCW(), 2),
                    bottomPos,
                    bottomPos.offset(facing.rotateY(), 2)
            };

            ItemEntity[] items = new ItemEntity[9];
            for (int i = 0; i < poses.length; i++) {
                List<ItemEntity> entities = this.level.getEntitiesWithinAABB(
                        ItemEntity.class, new AxisAlignedBB(poses[i]).grow(0.25), EntityPredicates.IS_ALIVE);
                if (entities.size() > 1)
                    return;
                if (entities.isEmpty())
                    continue;
                ItemEntity entity = entities.get(0);
                if (entity.cannotPickup())
                    return;
                ItemStack stack = entity.getItem();
                if (stack.isEmpty())
                    return;
                items[i] = entity;
                this.crafting.setInventorySlotContents(i, stack.copy());
            }

            IRecipe recipe = this.level.getRecipeManager().getRecipe(IRecipeType.CRAFTING, this.crafting, this.level).orElse(null);
            if (recipe == null)
                return;

            ItemStack result = recipe.getCraftingResult(this.crafting);
            if (result.isEmpty())
                return;
            ItemEntity resultItem = new ItemEntity(this.level,
                    this.worldPosition.getX() + 0.5F, this.worldPosition.getY() - 0.35F, this.worldPosition.getZ() + 0.5F, result.copy());
            resultItem.setMotion(0, 0, 0);
            this.level.addEntity(resultItem);

            NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(this.crafting);
            for (int i = 0; i < items.length; i++) {
                ItemEntity item = items[i];
                if (item == null)
                    continue;
                ItemStack stack = item.getItem();
                if (stack.getCount() <= 1)
                    item.remove();
                else {
                    stack.shrink(1);
                    item.setItem(stack);
                }

                ItemStack remain = remainingItems.get(i);
                if (!remain.isEmpty()) {
                    ItemEntity remItem = new ItemEntity(this.level, item.getPosX(), item.getPosY(), item.getPosZ(), remain.copy());
                    remItem.setMotion(0, 0, 0);
                    this.level.addEntity(remItem);
                }

                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.ANIMAL_SPAWNER));
            }
        }
    }
}
