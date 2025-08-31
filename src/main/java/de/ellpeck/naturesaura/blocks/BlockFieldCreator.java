package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFieldCreator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockFieldCreator extends BlockContainerImpl implements ICustomBlockState, IPickaxeBreakable {

    public BlockFieldCreator() {
        super("field_creator", BlockEntityFieldCreator.class, Properties.of().strength(2F).noOcclusion().sound(SoundType.STONE));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityFieldCreator) {
            if (!level.isClientSide) {
                var key = NaturesAura.MOD_ID + ":field_creator_pos";
                var compound = player.getPersistentData();
                if (!player.isShiftKeyDown() && compound.contains(key)) {
                    var stored = BlockPos.of(compound.getLong(key));
                    var creator = (BlockEntityFieldCreator) tile;
                    if (!pos.equals(stored)) {
                        if (creator.isCloseEnough(stored)) {
                            var otherTile = level.getBlockEntity(stored);
                            if (otherTile instanceof BlockEntityFieldCreator otherCreator) {
                                creator.connectionOffset = stored.subtract(pos);
                                creator.isMain = true;
                                creator.sendToClients();

                                otherCreator.connectionOffset = pos.subtract(stored);
                                otherCreator.isMain = false;
                                otherCreator.sendToClients();

                                compound.remove(key);
                                player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".connected"), true);
                            } else
                                player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".stored_pos_gone"), true);
                        } else
                            player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".too_far"), true);
                    } else
                        player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".same_position"), true);
                } else {
                    compound.putLong(key, pos.asLong());
                    player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".stored_pos"), true);
                }
            }
            return ItemInteractionResult.SUCCESS;
        } else
            return ItemInteractionResult.FAIL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityFieldCreator creator && creator.isCharged) {
            var connected = creator.getConnectedPos();
            if (connected != null)
                NaturesAuraAPI.instance().spawnParticleStream(
                    pos.getX() + 0.25F + rand.nextFloat() * 0.5F,
                    pos.getY() + 0.25F + rand.nextFloat() * 0.5F,
                    pos.getZ() + 0.25F + rand.nextFloat() * 0.5F,
                    connected.getX() + 0.25F + rand.nextFloat() * 0.5F,
                    connected.getY() + 0.25F + rand.nextFloat() * 0.5F,
                    connected.getZ() + 0.25F + rand.nextFloat() * 0.5F,
                    0.65F, 0x4245f4, 1F
                );
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

}
