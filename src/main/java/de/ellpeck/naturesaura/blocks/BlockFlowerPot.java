package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.INoItemBlock;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

import java.util.function.Supplier;

public class BlockFlowerPot extends FlowerPotBlock implements ICustomBlockState, IModItem, INoItemBlock {

    public BlockFlowerPot(Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties props) {
        super(emptyPot, block, props);
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().withExistingParent(this.getBaseName(), "block/flower_pot_cross").texture("plant", "block/" + BuiltInRegistries.BLOCK.getKey(this.getPotted()).getPath()).renderType("cutout"));
    }

    @Override
    public String getBaseName() {
        return "potted_" + BuiltInRegistries.BLOCK.getKey(this.getPotted()).getPath();
    }

}
