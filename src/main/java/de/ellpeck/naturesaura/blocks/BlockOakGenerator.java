package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOakGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.IAxeBreakable;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class BlockOakGenerator extends BlockContainerImpl implements IVisualizable, ICustomBlockState, IAxeBreakable {

    public BlockOakGenerator() {
        super("oak_generator", BlockEntityOakGenerator.class, Properties.of().strength(2F).sound(SoundType.WOOD));

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(BlockGrowFeatureEvent event) {
        var level = event.getLevel();
        var pos = event.getPos();
        if (level instanceof Level && !level.isClientSide() && IAuraType.forLevel((Level) level).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)
            && level.getBlockState(pos).getBlock() instanceof SaplingBlock) {
            Helper.getBlockEntitiesInArea(level, pos, 10, tile -> {
                if (!(tile instanceof BlockEntityOakGenerator oak))
                    return false;
                var replacement = BlockOakGenerator.getReplacement(event.getFeature());
                if (replacement != null) {
                    oak.scheduledBigTrees.add(pos);
                    event.setFeature(replacement);
                }
                return true;
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        return new AABB(pos).inflate(10);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0x2e7a11;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
            generator.modLoc("block/" + this.getBaseName()),
            generator.modLoc("block/" + this.getBaseName() + "_bottom"),
            generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> getReplacement(Holder<ConfiguredFeature<?, ?>> holder) {
        if (holder == null || !holder.unwrapKey().isPresent())
            return null;

        ResourceKey<ConfiguredFeature<?, ?>> feature = holder.unwrapKey().get();
        if (feature == TreeFeatures.FANCY_OAK || feature == TreeFeatures.FANCY_OAK_BEES) {
            return TreeFeatures.OAK;
        } else if (feature == TreeFeatures.FANCY_OAK_BEES_002) {
            return TreeFeatures.OAK_BEES_002;
        } else if (feature == TreeFeatures.FANCY_OAK_BEES_0002) {
            return TreeFeatures.OAK_BEES_0002;
        } else if (feature == TreeFeatures.FANCY_OAK_BEES_005) {
            return TreeFeatures.OAK_BEES_005;
        } else {
            return null;
        }
    }

}
