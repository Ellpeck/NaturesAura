package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockGeneratorLimitRemover extends BlockContainerImpl /*implements ITESRProvider*/ {

    public BlockGeneratorLimitRemover() {
        super("generator_limit_remover", TileEntityGeneratorLimitRemover::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    /*@Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityGeneratorLimitRemover.class, new RenderGeneratorLimitRemover());
    }*/
}
