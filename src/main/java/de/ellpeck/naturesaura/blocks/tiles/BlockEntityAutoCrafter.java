package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.BlockAutoCrafter;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlockEntityAutoCrafter extends BlockEntityImpl implements ITickableBlockEntity {

    public final CraftingContainer crafting = new CraftingContainer(new AbstractContainerMenu(null, 0) {
        @Override
        public boolean stillValid(Player playerIn) {
            return false;
        }
    }, 3, 3);

    public BlockEntityAutoCrafter(BlockPos pos, BlockState state) {
        super(ModTileEntities.AUTO_CRAFTER, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 60 != 0)
                return;
            if (!Multiblocks.AUTO_CRAFTER.isComplete(this.level, this.worldPosition))
                return;
            this.crafting.clearContent();

            var state = this.level.getBlockState(this.worldPosition);
            var facing = state.getValue(BlockAutoCrafter.FACING);
            var middlePos = this.worldPosition.above();
            var topPos = middlePos.relative(facing, 2);
            var bottomPos = middlePos.relative(facing.getOpposite(), 2);
            var poses = new BlockPos[]{
                    topPos.relative(facing.getCounterClockWise(), 2),
                    topPos,
                    topPos.relative(facing.getClockWise(), 2),
                    middlePos.relative(facing.getCounterClockWise(), 2),
                    middlePos,
                    middlePos.relative(facing.getClockWise(), 2),
                    bottomPos.relative(facing.getCounterClockWise(), 2),
                    bottomPos,
                    bottomPos.relative(facing.getClockWise(), 2)
            };

            var items = new ItemEntity[9];
            for (var i = 0; i < poses.length; i++) {
                var entities = this.level.getEntitiesOfClass(
                        ItemEntity.class, new AABB(poses[i]).inflate(0.25), EntitySelector.ENTITY_STILL_ALIVE);
                if (entities.size() > 1)
                    return;
                if (entities.isEmpty())
                    continue;
                var entity = entities.get(0);
                if (entity.hasPickUpDelay())
                    return;
                var stack = entity.getItem();
                if (stack.isEmpty())
                    return;
                items[i] = entity;
                this.crafting.setItem(i, stack.copy());
            }

            var recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, this.crafting, this.level).orElse(null);
            if (recipe == null)
                return;

            var result = recipe.assemble(this.crafting);
            if (result.isEmpty())
                return;
            var resultItem = new ItemEntity(this.level,
                    this.worldPosition.getX() + 0.5F, this.worldPosition.getY() - 0.35F, this.worldPosition.getZ() + 0.5F, result.copy());
            resultItem.setDeltaMovement(0, 0, 0);
            this.level.addFreshEntity(resultItem);

            var remainingItems = recipe.getRemainingItems(this.crafting);
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                if (item == null)
                    continue;
                var stack = item.getItem();
                if (stack.getCount() <= 1)
                    item.discard();
                else {
                    stack.shrink(1);
                    item.setItem(stack);
                }

                var remain = remainingItems.get(i);
                if (!remain.isEmpty()) {
                    var remItem = new ItemEntity(this.level, item.getX(), item.getY(), item.getZ(), remain.copy());
                    remItem.setDeltaMovement(0, 0, 0);
                    this.level.addFreshEntity(remItem);
                }

                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.ANIMAL_SPAWNER));
            }
        }
    }
}
