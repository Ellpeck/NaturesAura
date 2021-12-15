package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockCatalyst extends BlockImpl implements ICustomBlockState {

    public static final BooleanProperty NETHER = BlockNatureAltar.NETHER;

    public BlockCatalyst(String baseName, Properties properties) {
        super(baseName, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(NETHER, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean nether = IAuraType.forLevel(context.getLevel()).isSimilar(NaturesAuraAPI.TYPE_NETHER);
        return super.getStateForPlacement(context).setValue(NETHER, nether);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NETHER);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.models().cubeAll(this.getBaseName(), generator.modLoc("block/" + this.getBaseName()));
        generator.models().cubeAll(this.getBaseName() + "_nether", generator.modLoc("block/" + this.getBaseName() + "_nether"));
    }
}
