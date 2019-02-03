package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.BlockAutoCrafter;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TileEntityAutoCrafter extends TileEntityImpl implements ITickable {
    public final InventoryCrafting crafting = new InventoryCrafting(new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }
    }, 3, 3);


    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 60 != 0)
                return;
            if (!Multiblocks.AUTO_CRAFTER.isComplete(this.world, this.pos))
                return;
            this.crafting.clear();

            IBlockState state = this.world.getBlockState(this.pos);
            EnumFacing facing = state.getValue(BlockAutoCrafter.FACING);
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

            EntityItem[] items = new EntityItem[9];
            for (int i = 0; i < poses.length; i++) {
                List<EntityItem> entities = this.world.getEntitiesWithinAABB(
                        EntityItem.class, new AxisAlignedBB(poses[i]).grow(0.25), EntitySelectors.IS_ALIVE);
                if (entities.size() > 1)
                    return;
                if (entities.isEmpty())
                    continue;
                EntityItem entity = entities.get(0);
                if (entity.cannotPickup())
                    return;
                ItemStack stack = entity.getItem();
                if (stack.isEmpty())
                    return;
                items[i] = entity;
                this.crafting.setInventorySlotContents(i, stack.copy());
            }

            IRecipe recipe = CraftingManager.findMatchingRecipe(this.crafting, this.world);
            if (recipe == null)
                return;

            ItemStack result = recipe.getCraftingResult(this.crafting);
            if (result.isEmpty())
                return;
            EntityItem resultItem = new EntityItem(this.world,
                    this.pos.getX() + 0.5F, this.pos.getY() - 0.35F, this.pos.getZ() + 0.5F, result.copy());
            resultItem.motionX = resultItem.motionY = resultItem.motionZ = 0;
            this.world.spawnEntity(resultItem);

            NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(this.crafting);
            for (int i = 0; i < items.length; i++) {
                EntityItem item = items[i];
                if (item == null)
                    continue;
                ItemStack stack = item.getItem();
                if (stack.getCount() <= 1)
                    item.setDead();
                else {
                    stack.shrink(1);
                    item.setItem(stack);
                }

                ItemStack remain = remainingItems.get(i);
                if (!remain.isEmpty()) {
                    EntityItem remItem = new EntityItem(this.world, item.posX, item.posY, item.posZ, remain.copy());
                    remItem.motionX = remItem.motionY = remItem.motionZ = 0;
                    this.world.spawnEntity(remItem);
                }

                PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 19));
            }
        }
    }
}
