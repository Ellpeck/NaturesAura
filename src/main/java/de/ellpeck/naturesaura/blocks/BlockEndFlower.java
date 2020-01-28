package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityEndFlower;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEndFlower extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public BlockEndFlower() {
        super(ModBlocks.prop(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.PLANT));
        MinecraftForge.EVENT_BUS.register(this);
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(TileEntityEndFlower::new, this));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vec3d vec3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vec3d.x, vec3d.y, vec3d.z);
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
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() == Blocks.END_STONE;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getBlock() == Blocks.END_STONE;
    }

    @Override
    public String getBaseName() {
        return "end_flower";
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityEndFlower();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity tile = builder.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof TileEntityEndFlower && ((TileEntityEndFlower) tile).isDrainMode)
            return NonNullList.create();
        return super.getDrops(state, builder);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cross(this.getBaseName(), generator.modLoc("block/" + this.getBaseName())));
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "block/" + this.getBaseName());
    }
}
