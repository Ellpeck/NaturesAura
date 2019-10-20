package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityFieldCreator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFieldCreator extends BlockContainerImpl {
    public BlockFieldCreator() {
        super(Material.ROCK, "field_creator", TileEntityFieldCreator.class, "field_creator");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityFieldCreator) {
            if (!worldIn.isRemote) {
                String key = NaturesAura.MOD_ID + ":field_creator_pos";
                CompoundNBT compound = playerIn.getEntityData();
                if (!playerIn.isSneaking() && compound.hasKey(key)) {
                    BlockPos stored = BlockPos.fromLong(compound.getLong(key));
                    TileEntityFieldCreator creator = (TileEntityFieldCreator) tile;
                    if (!pos.equals(stored)) {
                        if (creator.isCloseEnough(stored)) {
                            TileEntity otherTile = worldIn.getTileEntity(stored);
                            if (otherTile instanceof TileEntityFieldCreator) {
                                creator.connectionOffset = stored.subtract(pos);
                                creator.isMain = true;
                                creator.sendToClients();

                                TileEntityFieldCreator otherCreator = (TileEntityFieldCreator) otherTile;
                                otherCreator.connectionOffset = pos.subtract(stored);
                                otherCreator.isMain = false;
                                otherCreator.sendToClients();

                                compound.removeTag(key);
                                playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".connected"), true);
                            } else
                                playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".stored_pos_gone"), true);
                        } else
                            playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".too_far"), true);
                    } else
                        playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".same_position"), true);
                } else {
                    compound.setLong(key, pos.toLong());
                    playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".stored_pos"), true);
                }
            }
            return true;
        } else
            return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityFieldCreator) {
            TileEntityFieldCreator creator = (TileEntityFieldCreator) tile;
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
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(BlockState baseState, IBlockAccess world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
}
