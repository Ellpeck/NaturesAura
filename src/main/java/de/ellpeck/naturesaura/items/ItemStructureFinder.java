package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityStructureFinder;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ItemStructureFinder extends ItemImpl {

    private final Holder<Structure> structure;
    private final int color;
    private final int radius;

    public ItemStructureFinder(String baseName, Holder<Structure> structure, int color, int radius) {
        super(baseName);
        this.structure = structure;
        this.color = color;
        this.radius = radius;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && ((ServerLevel) levelIn).structureManager().shouldGenerateStructures()) {
            var holderSet = HolderSet.direct(this.structure);
            var pos = ((ServerLevel) levelIn).getChunkSource().getGenerator().findNearestMapStructure((ServerLevel) levelIn, holderSet, playerIn.blockPosition(), this.radius, false);
            if (pos != null) {
                var entity = new EntityStructureFinder(ModEntities.STRUCTURE_FINDER, levelIn);
                entity.setPos(playerIn.getX(), playerIn.getY(0.5D), playerIn.getZ());
                entity.setItem(stack);
                entity.getEntityData().set(EntityStructureFinder.COLOR, this.color);
                entity.signalTo(pos.getFirst().above(64));
                levelIn.addFreshEntity(entity);

                stack.shrink(1);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
