package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFieldCreator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

public class BlockFieldCreator extends BlockContainerImpl implements ICustomBlockState, ICustomRenderType {
    public BlockFieldCreator() {
        super("field_creator", BlockEntityFieldCreator::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).notSolid().sound(SoundType.STONE));
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityFieldCreator) {
            if (!levelIn.isClientSide) {
                String key = NaturesAura.MOD_ID + ":field_creator_pos";
                CompoundTag compound = player.getPersistentData();
                if (!player.isSneaking() && compound.contains(key)) {
                    BlockPos stored = BlockPos.fromLong(compound.getLong(key));
                    BlockEntityFieldCreator creator = (BlockEntityFieldCreator) tile;
                    if (!pos.equals(stored)) {
                        if (creator.isCloseEnough(stored)) {
                            BlockEntity otherTile = levelIn.getBlockEntity(stored);
                            if (otherTile instanceof BlockEntityFieldCreator) {
                                creator.connectionOffset = stored.subtract(pos);
                                creator.isMain = true;
                                creator.sendToClients();

                                BlockEntityFieldCreator otherCreator = (BlockEntityFieldCreator) otherTile;
                                otherCreator.connectionOffset = pos.subtract(stored);
                                otherCreator.isMain = false;
                                otherCreator.sendToClients();

                                compound.remove(key);
                                player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".connected"), true);
                            } else
                                player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".stored_pos_gone"), true);
                        } else
                            player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".too_far"), true);
                    } else
                        player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".same_position"), true);
                } else {
                    compound.putLong(key, pos.toLong());
                    player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".stored_pos"), true);
                }
            }
            return InteractionResult.SUCCESS;
        } else
            return InteractionResult.FAIL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityFieldCreator) {
            BlockEntityFieldCreator creator = (BlockEntityFieldCreator) tile;
            if (creator.isCharged) {
                BlockPos connected = creator.getConnectedPos();
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
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
    }
}
