package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityItemDistributor;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.Player;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.level.Level;

public class BlockItemDistributor extends BlockContainerImpl implements ICustomBlockState {

    public BlockItemDistributor() {
        super("item_distributor", BlockEntityItemDistributor::new, Properties.from(Blocks.STONE_BRICKS));
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand handIn, BlockRayTraceResult hit) {
        if (!player.isSneaking())
            return InteractionResult.FAIL;
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (!(tile instanceof BlockEntityItemDistributor))
            return InteractionResult.FAIL;
        if (!levelIn.isClientSide) {
            BlockEntityItemDistributor distributor = (BlockEntityItemDistributor) tile;
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
