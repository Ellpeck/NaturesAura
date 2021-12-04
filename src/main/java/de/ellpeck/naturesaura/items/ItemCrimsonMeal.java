package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.gen.ModFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.level.Level;
import net.minecraft.level.gen.feature.IFeatureConfig;
import net.minecraft.level.server.ServerLevel;

public class ItemCrimsonMeal extends ItemImpl {
    public ItemCrimsonMeal() {
        super("crimson_meal");
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.NETHER_WART) {
            if (!level.isClientSide) {
                if (level.rand.nextInt(5) == 0) {
                    int age = state.get(NetherWartBlock.AGE);
                    if (age >= 3) {
                        ModFeatures.NETHER_WART_MUSHROOM.func_241855_a((ServerLevel) level, ((ServerLevel) level).getChunkProvider().getChunkGenerator(), level.rand, pos, IFeatureConfig.NO_FEATURE_CONFIG);
                    } else {
                        level.setBlockState(pos, state.with(NetherWartBlock.AGE, age + 1));
                    }
                }
                level.playEvent(2005, pos, 0);
                context.getItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else if (level.getBlockState(pos.up()).isAir(level, pos.up()) && level.getBlockState(pos).getBlock() == Blocks.SOUL_SAND) {
            if (!level.isClientSide) {
                for (int i = level.rand.nextInt(5); i >= 0; i--) {
                    BlockPos offset = pos.add(MathHelper.nextInt(level.rand, -3, 3), 1, MathHelper.nextInt(level.rand, -3, 3));
                    if (level.getBlockState(offset.down()).getBlock() == Blocks.SOUL_SAND && level.getBlockState(offset).isAir(level, offset)) {
                        level.setBlockState(offset, Blocks.NETHER_WART.getDefaultState());
                    }
                }
                level.playEvent(2005, pos, 0);
                context.getItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
