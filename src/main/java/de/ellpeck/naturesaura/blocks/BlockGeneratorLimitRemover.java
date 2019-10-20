package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGeneratorLimitRemover extends BlockContainerImpl implements ITESRProvider {

    public BlockGeneratorLimitRemover() {
        super(Material.ROCK, "generator_limit_remover", TileEntityGeneratorLimitRemover.class, "generator_limit_remover");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityGeneratorLimitRemover.class, new RenderGeneratorLimitRemover());
    }
}
