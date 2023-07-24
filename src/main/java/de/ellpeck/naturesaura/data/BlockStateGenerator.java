package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NaturesAura.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var item : ModRegistry.ALL_ITEMS) {
            if (!(item instanceof Block block))
                continue;
            if (block instanceof ICustomBlockState custom) {
                custom.generateCustomBlockState(this);
            } else {
                this.simpleBlock(block);
            }
        }
    }
}
