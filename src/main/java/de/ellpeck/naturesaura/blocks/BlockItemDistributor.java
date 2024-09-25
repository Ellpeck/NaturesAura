package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityItemDistributor;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockItemDistributor extends BlockContainerImpl implements ICustomBlockState {

    public BlockItemDistributor() {
        super("item_distributor", BlockEntityItemDistributor.class, Properties.ofFullCopy(Blocks.STONE_BRICKS));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown())
            return InteractionResult.FAIL;
        var tile = level.getBlockEntity(pos);
        if (!(tile instanceof BlockEntityItemDistributor))
            return InteractionResult.FAIL;
        if (!level.isClientSide) {
            var distributor = (BlockEntityItemDistributor) tile;
            distributor.isRandomMode = !distributor.isRandomMode;
            distributor.sendToClients();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
            generator.modLoc("block/" + this.getBaseName()),
            generator.modLoc("block/" + this.getBaseName() + "_bottom"),
            generator.modLoc("block/" + this.getBaseName() + "_top")));
    }

}
