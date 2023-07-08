package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySlimeSplitGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockSlimeSplitGenerator extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    public BlockSlimeSplitGenerator() {
        super("slime_split_generator", BlockEntitySlimeSplitGenerator.class, Properties.copy(Blocks.SLIME_BLOCK).strength(2));
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        return new AABB(pos).inflate(8);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0x4da84f;
    }

    private static class Events {

        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event) {
            var entity = event.getEntity();
            if (!(entity instanceof Slime slime) || entity.level().isClientSide)
                return;
            var size = slime.getSize();
            if (size <= 1)
                return;
            Helper.getBlockEntitiesInArea(entity.level(), entity.blockPosition(), 8, tile -> {
                if (!(tile instanceof BlockEntitySlimeSplitGenerator gen))
                    return false;
                if (gen.isBusy())
                    return false;
                gen.startGenerating(slime);
                return true;
            });
        }

    }
}
