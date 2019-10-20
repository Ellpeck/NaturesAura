package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityEndFlower;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BushBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class BlockEndFlower extends BushBlock implements IModItem, ICreativeItem, IModelProvider, ITileEntityProvider {

    public BlockEndFlower() {
        this.setHardness(0.5F);
        this.setSoundType(SoundType.PLANT);
        MinecraftForge.EVENT_BUS.register(this);

        ModRegistry.add(this);

    }

    @SubscribeEvent
    public void onDraonTick(LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living.world.isRemote || !(living instanceof EnderDragonEntity))
            return;
        EnderDragonEntity dragon = (EnderDragonEntity) living;
        if (dragon.deathTicks < 150 || dragon.deathTicks % 10 != 0)
            return;

        for (int i = 0; i < 6; i++) {
            int x = dragon.world.rand.nextInt(256) - 128;
            int z = dragon.world.rand.nextInt(256) - 128;
            BlockPos pos = new BlockPos(x, dragon.world.getHeight(x, z), z);
            if (!dragon.world.isBlockLoaded(pos))
                continue;
            if (dragon.world.getBlockState(pos.down()).getBlock() != Blocks.END_STONE)
                continue;
            dragon.world.setBlockState(pos, this.getDefaultState());
        }
    }

    @Override
    protected boolean canSustainBush(BlockState state) {
        return state.getBlock() == Blocks.END_STONE;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, BlockState state) {
        return this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    public String getBaseName() {
        return "end_flower";
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityEndFlower.class, new ResourceLocation(NaturesAura.MOD_ID, "end_flower"));
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEndFlower();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityEndFlower && ((TileEntityEndFlower) tile).isDrainMode)
            return;

        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }
}
