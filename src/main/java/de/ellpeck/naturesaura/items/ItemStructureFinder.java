package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityStructureFinder;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class ItemStructureFinder extends ItemImpl {
    private final Structure structureName;
    private final int color;

    public ItemStructureFinder(String baseName, Structure structureName, int color) {
        super(baseName);
        this.structureName = structureName;
        this.color = color;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            BlockPos pos = ((ServerWorld) worldIn).getChunkProvider().getChunkGenerator().func_235956_a_((ServerWorld) worldIn, this.structureName, playerIn.getPosition(), 1024, false);
            if (pos != null) {
                EntityStructureFinder entity = new EntityStructureFinder(ModEntities.STRUCTURE_FINDER, worldIn);
                entity.setPosition(playerIn.getPosX(), playerIn.getPosYHeight(0.5D), playerIn.getPosZ());
                entity.func_213863_b(stack);
                entity.getDataManager().set(EntityStructureFinder.COLOR, this.color);
                entity.moveTowards(pos.up(64));
                worldIn.addEntity(entity);

                stack.shrink(1);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
