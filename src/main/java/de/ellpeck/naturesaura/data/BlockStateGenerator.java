package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, NaturesAura.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (IModItem item : ModRegistry.ALL_ITEMS) {
            if (!(item instanceof Block))
                continue;
            Block block = (Block) item;
            if (block instanceof ICustomBlockState) {
                ((ICustomBlockState) block).generateCustomBlockState(this);
            } else {
                this.simpleBlock(block);
            }
        }
    }
}
