package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class BlockGeneratorLimitRemover extends BlockContainerImpl implements ITESRProvider {

    public BlockGeneratorLimitRemover() {
        super("generator_limit_remover", TileEntityGeneratorLimitRemover::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<TileEntityType, Function<TileEntityRendererDispatcher, TileEntityRenderer<? extends TileEntity>>> getTESR() {
        return new Tuple<>(ModTileEntities.GENERATOR_LIMIT_REMOVER, RenderGeneratorLimitRemover::new);
    }
}
