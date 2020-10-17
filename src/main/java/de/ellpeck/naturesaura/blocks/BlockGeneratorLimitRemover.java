package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Tuple;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockGeneratorLimitRemover extends BlockContainerImpl implements ITESRProvider<TileEntityGeneratorLimitRemover>, ICustomBlockState {

    public BlockGeneratorLimitRemover() {
        super("generator_limit_remover", TileEntityGeneratorLimitRemover::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    @Override
    public Tuple<TileEntityType<TileEntityGeneratorLimitRemover>, Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super TileEntityGeneratorLimitRemover>>>> getTESR() {
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
