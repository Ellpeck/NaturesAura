package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockGoldenLeaves extends BlockLeaves implements
        IModItem, IModelProvider, IColorProvidingBlock, IColorProvidingItem {

    private static final int HIGHEST_STAGE = 3;
    private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, HIGHEST_STAGE);

    public BlockGoldenLeaves() {
        this.leavesFancy = true;
        ModRegistry.addItemOrBlock(this);
    }

    @Override
    public String getBaseName() {
        return "golden_leaves";
    }

    @Override
    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(STAGE) == HIGHEST_STAGE && rand.nextFloat() >= 0.75F)
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + rand.nextFloat(),
                    0F, 0F, 0F,
                    0xF2FF00, 0.5F + rand.nextFloat(), 50, 0F, false, true);
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        NonNullList<ItemStack> drops = NonNullList.create();
        this.getDrops(drops, world, pos, world.getBlockState(pos), fortune);
        return drops;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE, STAGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean check = (meta & 4) != 0; // 4th bit
        boolean decay = (meta & 8) != 0; // 3rd bit
        int stage = meta & HIGHEST_STAGE; // 1st and 2nd bit

        return this.getDefaultState().withProperty(CHECK_DECAY, check).withProperty(DECAYABLE, decay).withProperty(STAGE, stage);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean check = state.getValue(CHECK_DECAY);
        boolean decay = state.getValue(DECAYABLE);

        return (check ? 1 : 0) << 3 | (decay ? 1 : 0) << 2 | state.getValue(STAGE);
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos) {
        if (!state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
            world.setBlockState(pos, state.withProperty(CHECK_DECAY, true), 4);
        }
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> {
            int color = 0xF2FF00;
            if (state != null && worldIn != null && pos != null) {
                int foliage = BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
                return Helper.blendColors(color, foliage, state.getValue(STAGE) / (float) HIGHEST_STAGE);
            } else {
                return color;
            }
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        if (state.getValue(STAGE) < HIGHEST_STAGE) {
            if (rand.nextFloat() >= 0.75F) {
                drops.add(new ItemStack(ModItems.GOLD_FIBER));
            }
        } else if (rand.nextFloat() >= 0.25F) {
            drops.add(new ItemStack(ModItems.GOLD_LEAF));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (!worldIn.isRemote) {
            int stage = state.getValue(STAGE);
            if (stage < HIGHEST_STAGE) {
                worldIn.setBlockState(pos, state.withProperty(STAGE, stage + 1));
            }

            if (stage > 1) {
                EnumFacing facing = EnumFacing.random(rand);
                convert(worldIn, pos.offset(facing));
            }
        }
    }

    public static boolean convert(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isLeaves(state, world, pos) &&
                !(state.getBlock() instanceof BlockAncientLeaves || state.getBlock() instanceof BlockGoldenLeaves)) {
            if (!world.isRemote) {
                world.setBlockState(pos, ModBlocks.GOLDEN_LEAVES.getDefaultState()
                        .withProperty(CHECK_DECAY, state.getPropertyKeys().contains(CHECK_DECAY) ? state.getValue(CHECK_DECAY) : false)
                        .withProperty(DECAYABLE, state.getPropertyKeys().contains(DECAYABLE) ? state.getValue(DECAYABLE) : false));
            }
            return true;
        }
        return false;
    }
}