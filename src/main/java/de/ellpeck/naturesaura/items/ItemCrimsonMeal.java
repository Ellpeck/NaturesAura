package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.gen.WorldGenNetherWartMushroom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.server.ServerWorld;

public class ItemCrimsonMeal extends ItemImpl {
    public ItemCrimsonMeal() {
        super("crimson_meal");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == Blocks.NETHER_WART) {
            if (!world.isRemote) {
                if (world.rand.nextInt(5) == 0) {
                    int age = state.get(NetherWartBlock.AGE);
                    if (age >= 3) {
                        new WorldGenNetherWartMushroom().place(world, ((ServerWorld) world).getChunkProvider().getChunkGenerator(), world.rand, pos, IFeatureConfig.NO_FEATURE_CONFIG);
                    } else {
                        world.setBlockState(pos, state.with(NetherWartBlock.AGE, age + 1));
                    }
                }
                world.playEvent(2005, pos, 0);
                context.getItem().shrink(1);
            }
            return ActionResultType.SUCCESS;
        } else if (world.getBlockState(pos.up()).isAir(world, pos.up()) && world.getBlockState(pos).getBlock() == Blocks.SOUL_SAND) {
            if (!world.isRemote) {
                for (int i = world.rand.nextInt(5); i >= 0; i--) {
                    BlockPos offset = pos.add(MathHelper.nextInt(world.rand, -3, 3), 1, MathHelper.nextInt(world.rand, -3, 3));
                    if (world.getBlockState(offset.down()).getBlock() == Blocks.SOUL_SAND && world.getBlockState(offset).isAir(world, offset)) {
                        world.setBlockState(offset, Blocks.NETHER_WART.getDefaultState());
                    }
                }
                world.playEvent(2005, pos, 0);
                context.getItem().shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
}
