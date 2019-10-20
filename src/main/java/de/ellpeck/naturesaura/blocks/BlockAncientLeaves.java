package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAncientLeaves;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockAncientLeaves extends LeavesBlock implements
        IModItem, ICreativeItem, IModelProvider, IColorProvidingBlock, IColorProvidingItem, ITileEntityProvider {

    public BlockAncientLeaves() {
        this.leavesFancy = true;
        ModRegistry.add(this);
    }

    @Override
    public MaterialColor getMapColor(BlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MaterialColor.PINK;
    }

    @Override
    public String getBaseName() {
        return "ancient_leaves";
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityAncientLeaves.class, new ResourceLocation(NaturesAura.MOD_ID, "ancient_leaves"));
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this, 1, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        boolean check = (meta & 1) != 0;
        boolean decay = (meta & 2) != 0;

        return this.getDefaultState().withProperty(CHECK_DECAY, check).withProperty(DECAYABLE, decay);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        boolean check = state.getValue(CHECK_DECAY);
        boolean decay = state.getValue(DECAYABLE);

        return (check ? 1 : 0) | (decay ? 1 : 0) << 1;
    }

    @Override
    public void beginLeavesDecay(BlockState state, World world, BlockPos pos) {
        if (!state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
            world.setBlockState(pos, state.withProperty(CHECK_DECAY, true), 4);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return (meta & 2) != 0 ? new TileEntityAncientLeaves() : null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (rand.nextFloat() >= 0.95F && !worldIn.getBlockState(pos.down()).isFullBlock()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityAncientLeaves) {
                if (((TileEntityAncientLeaves) tile).getAuraContainer(null).getStoredAura() > 0) {
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            pos.getX() + rand.nextDouble(), pos.getY(), pos.getZ() + rand.nextDouble(),
                            0F, 0F, 0F, 0xCC4780,
                            rand.nextFloat() * 2F + 0.5F,
                            rand.nextInt(50) + 75,
                            rand.nextFloat() * 0.02F + 0.002F, true, true);

                }
            }
        }
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.ANCIENT_SAPLING);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityAncientLeaves) {
                if (((TileEntityAncientLeaves) tile).getAuraContainer(null).getStoredAura() <= 0) {
                    worldIn.setBlockState(pos, ModBlocks.DECAYED_LEAVES.getDefaultState());
                }
            }
        }
    }
}
