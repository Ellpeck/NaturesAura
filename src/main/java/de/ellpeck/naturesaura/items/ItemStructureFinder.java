package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityStructureFinder;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class ItemStructureFinder extends ItemImpl {

    private final StructureFeature<?> structureName;
    private final int color;
    private final int radius;

    public ItemStructureFinder(String baseName, StructureFeature<?> structureName, int color, int radius) {
        super(baseName);
        this.structureName = structureName;
        this.color = color;
        this.radius = radius;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && ((ServerLevel) levelIn).structureFeatureManager().shouldGenerateFeatures()) {
            BlockPos pos = ((ServerLevel) levelIn).getChunkSource().getGenerator().findNearestMapFeature((ServerLevel) levelIn, this.structureName, playerIn.getPosition(), this.radius, false);
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
