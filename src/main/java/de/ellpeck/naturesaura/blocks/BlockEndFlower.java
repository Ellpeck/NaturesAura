package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityEndFlower;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import de.ellpeck.naturesaura.reg.ModTileType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

public class BlockEndFlower extends BushBlock implements IModItem, IModelProvider {

    public BlockEndFlower() {
        super(ModBlocks.prop(Material.PLANTS).hardnessAndResistance(0.5F).sound(SoundType.PLANT));
        MinecraftForge.EVENT_BUS.register(this);
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(TileEntityEndFlower::new, this));
    }

    @SubscribeEvent
    public void onDragonTick(LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living.world.isRemote || !(living instanceof EnderDragonEntity))
            return;
        EnderDragonEntity dragon = (EnderDragonEntity) living;
        if (dragon.deathTicks < 150 || dragon.deathTicks % 10 != 0)
            return;

        for (int i = 0; i < 6; i++) {
            int x = dragon.world.rand.nextInt(256) - 128;
            int z = dragon.world.rand.nextInt(256) - 128;
            BlockPos pos = new BlockPos(x, dragon.world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z), z);
            if (!dragon.world.isBlockLoaded(pos))
                continue;
            if (dragon.world.getBlockState(pos.down()).getBlock() != Blocks.END_STONE)
                continue;
            dragon.world.setBlockState(pos, this.getDefaultState());
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        return state.getBlock() == Blocks.END_STONE;
    }

    @Override
    public String getBaseName() {
        return "end_flower";
    }

    /*
    @Override
    public void onInit(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityEndFlower.class, new ResourceLocation(NaturesAura.MOD_ID, "end_flower"));
    }
     */

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityEndFlower();
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
