package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySlimeSplitGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockSlimeSplitGenerator extends BlockContainerImpl implements IVisualizable, ICustomBlockState {
    public BlockSlimeSplitGenerator() {
        super("slime_split_generator", TileEntitySlimeSplitGenerator::new, Properties.from(Blocks.SLIME_BLOCK).hardnessAndResistance(2));
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
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(8);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x4da84f;
    }

    private static class Events {

        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (!(entity instanceof SlimeEntity) || entity.world.isRemote)
                return;
            SlimeEntity slime = (SlimeEntity) entity;
            int size = slime.getSlimeSize();
            if (size <= 1)
                return;
            Helper.getTileEntitiesInArea(entity.world, entity.getPosition(), 8, tile -> {
                if (!(tile instanceof TileEntitySlimeSplitGenerator))
                    return false;
                TileEntitySlimeSplitGenerator gen = (TileEntitySlimeSplitGenerator) tile;
                if (gen.isBusy())
                    return false;
                gen.startGenerating(slime);
                return true;
            });
        }

    }
}
