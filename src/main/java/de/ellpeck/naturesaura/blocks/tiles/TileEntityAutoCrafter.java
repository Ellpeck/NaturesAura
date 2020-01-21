package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.BlockAutoCrafter;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TileEntityAutoCrafter extends TileEntityImpl implements ITickableTileEntity {
    public final CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return false;
        }
    }, 3, 3);

    public TileEntityAutoCrafter(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 60 != 0)
                return;
            if (!Multiblocks.AUTO_CRAFTER.isComplete(this.world, this.pos))
                return;
            this.crafting.clear();

            BlockState state = this.world.getBlockState(this.pos);
            Direction facing = state.get(BlockAutoCrafter.FACING);
            BlockPos middlePos = this.pos.up();
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
                List<ItemEntity> entities = this.world.getEntitiesWithinAABB(
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

            // TODO get recipes from the recipe registry??
            IRecipe recipe = /*CraftingManager.findMatchingRecipe(this.crafting, this.world);*/null;
            if (recipe == null)
                return;

            ItemStack result = recipe.getCraftingResult(this.crafting);
            if (result.isEmpty())
                return;
            ItemEntity resultItem = new ItemEntity(this.world,
                    this.pos.getX() + 0.5F, this.pos.getY() - 0.35F, this.pos.getZ() + 0.5F, result.copy());
            resultItem.setMotion(0, 0, 0);
            this.world.addEntity(resultItem);

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
                    ItemEntity remItem = new ItemEntity(this.world, item.posX, item.posY, item.posZ, remain.copy());
                    remItem.setMotion(0, 0, 0);
                    this.world.addEntity(remItem);
                }

                // TODO particles
               /* PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 19));*/
            }
        }
    }
}
