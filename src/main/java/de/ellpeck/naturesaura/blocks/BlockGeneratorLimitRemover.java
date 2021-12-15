package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderGeneratorLimitRemover;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class BlockGeneratorLimitRemover extends BlockContainerImpl implements ITESRProvider<BlockEntityGeneratorLimitRemover>, ICustomBlockState {

    public BlockGeneratorLimitRemover() {
        super("generator_limit_remover", BlockEntityGeneratorLimitRemover::new, Properties.of(Material.STONE).strength(2F).sound(SoundType.STONE));
    }

    @Override
    public Tuple<BlockEntityType<? extends BlockEntityGeneratorLimitRemover>, Supplier<BlockEntityRendererProvider<BlockEntityGeneratorLimitRemover>>> getTESR() {
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
