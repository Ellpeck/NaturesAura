package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BlockFlowerPot extends FlowerPotBlock implements ICustomBlockState, IModItem, INoItemBlock, ICustomRenderType {

    public BlockFlowerPot(Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties props) {
        super(emptyPot, block, props);
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models()
                .withExistingParent(this.getBaseName(), "block/flower_pot_cross")
                .texture("plant", "block/" + ForgeRegistries.BLOCKS.getKey(this.getContent()).getPath()));
    }

    @Override
    public String getBaseName() {
        return "potted_" + ForgeRegistries.BLOCKS.getKey(this.getContent()).getPath();
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::cutout;
    }
}
