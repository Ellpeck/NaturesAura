package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.gen.ModFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;

public class ItemCrimsonMeal extends ItemImpl {

    public ItemCrimsonMeal() {
        super("crimson_meal");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.NETHER_WART) {
            if (!level.isClientSide) {
                if (level.random.nextInt(5) == 0) {
                    int age = state.getValue(NetherWartBlock.AGE);
                    if (age >= 3) {
                        ModFeatures.Configured.NETHER_WART_MUSHROOM.value().place((ServerLevel) level, ((ServerLevel) level).getChunkSource().getGenerator(), level.random, pos);
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(NetherWartBlock.AGE, age + 1));
                    }
                }
                level.levelEvent(2005, pos, 0);
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (level.getBlockState(pos.above()).isAir() && level.getBlockState(pos).getBlock() == Blocks.SOUL_SAND) {
            if (!level.isClientSide) {
                for (var i = level.random.nextInt(5); i >= 0; i--) {
                    var offset = pos.offset(Mth.nextInt(level.random, -3, 3), 1, Mth.nextInt(level.random, -3, 3));
                    if (level.getBlockState(offset.below()).getBlock() == Blocks.SOUL_SAND && level.getBlockState(offset).isAir()) {
                        level.setBlockAndUpdate(offset, Blocks.NETHER_WART.defaultBlockState());
                    }
                }
                level.levelEvent(2005, pos, 0);
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
