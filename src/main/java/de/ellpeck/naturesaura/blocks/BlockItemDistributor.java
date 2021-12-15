package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityItemDistributor;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockItemDistributor extends BlockContainerImpl implements ICustomBlockState {

    public BlockItemDistributor() {
        super("item_distributor", BlockEntityItemDistributor::new, Properties.copy(Blocks.STONE_BRICKS));
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!player.isCrouching())
            return InteractionResult.FAIL;
        var tile = levelIn.getBlockEntity(pos);
        if (!(tile instanceof BlockEntityItemDistributor))
            return InteractionResult.FAIL;
        if (!levelIn.isClientSide) {
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
