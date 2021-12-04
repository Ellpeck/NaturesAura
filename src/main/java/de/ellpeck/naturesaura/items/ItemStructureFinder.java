package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityStructureFinder;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraft.level.gen.feature.structure.Structure;
import net.minecraft.level.server.ServerLevel;

public class ItemStructureFinder extends ItemImpl {
    private final Structure structureName;
    private final int color;
    private final int radius;

    public ItemStructureFinder(String baseName, Structure structureName, int color, int radius) {
        super(baseName);
        this.structureName = structureName;
        this.color = color;
        this.radius = radius;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        // ServerLevel.getStructureManager().doesGenerateFeatures()
        if (!levelIn.isClientSide && ((ServerLevel) levelIn).func_241112_a_().func_235005_a_()) {
            BlockPos pos = ((ServerLevel) levelIn).getChunkProvider().getChunkGenerator().func_235956_a_((ServerLevel) levelIn, this.structureName, playerIn.getPosition(), this.radius, false);
            if (pos != null) {
                EntityStructureFinder entity = new EntityStructureFinder(ModEntities.STRUCTURE_FINDER, levelIn);
                entity.setPosition(playerIn.getPosX(), playerIn.getPosYHeight(0.5D), playerIn.getPosZ());
                entity.func_213863_b(stack);
                entity.getDataManager().set(EntityStructureFinder.COLOR, this.color);
                entity.moveTowards(pos.up(64));
                levelIn.addEntity(entity);

                stack.shrink(1);
            }
        }
        return new ActionResult<>(InteractionResult.SUCCESS, stack);
    }
}
