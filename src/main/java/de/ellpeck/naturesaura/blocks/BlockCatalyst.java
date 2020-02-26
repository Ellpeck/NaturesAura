package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;

public class BlockCatalyst extends BlockImpl implements ICustomBlockState {
    public static final BooleanProperty NETHER = BlockNatureAltar.NETHER;

    public BlockCatalyst(String baseName, Properties properties) {
        super(baseName, properties);
        this.setDefaultState(this.getDefaultState().with(NETHER, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean nether = IAuraType.forWorld(context.getWorld()).isSimilar(NaturesAuraAPI.TYPE_NETHER);
        return super.getStateForPlacement(context).with(NETHER, nether);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NETHER);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.models().cubeAll(this.getBaseName(), generator.modLoc("block/" + this.getBaseName()));
        generator.models().cubeAll(this.getBaseName() + "_nether", generator.modLoc("block/" + this.getBaseName() + "_nether"));
    }
}
