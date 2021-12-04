package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.tileentity.BlockEntityType;
import net.minecraft.util.Tuple;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockGeneratorLimitRemover extends BlockContainerImpl implements ITESRProvider<BlockEntityGeneratorLimitRemover>, ICustomBlockState {

    public BlockGeneratorLimitRemover() {
        super("generator_limit_remover", BlockEntityGeneratorLimitRemover::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    @Override
    public Tuple<BlockEntityType<BlockEntityGeneratorLimitRemover>, Supplier<Function<? super BlockEntityRendererDispatcher, ? extends BlockEntityRenderer<? super BlockEntityGeneratorLimitRemover>>>> getTESR() {
        return new Tuple<>(ModTileEntities.GENERATOR_LIMIT_REMOVER, () -> RenderGeneratorLimitRemover::new);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
