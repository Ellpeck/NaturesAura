package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEndFlower;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevelReader;
import net.minecraft.level.Level;
import net.minecraft.level.gen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockEndFlower extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public BlockEndFlower() {
        super(Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.PLANT));
        MinecraftForge.EVENT_BUS.register(this);
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(BlockEntityEndFlower::new, this));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        Vector3d vec3d = state.getOffset(levelIn, pos);
        return SHAPE.withOffset(vec3d.x, vec3d.y, vec3d.z);
    }

    @SubscribeEvent
    public void onDragonTick(LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living.level.isClientSide || !(living instanceof EnderDragonEntity))
            return;
        EnderDragonEntity dragon = (EnderDragonEntity) living;
        if (dragon.deathTicks < 150 || dragon.deathTicks % 10 != 0)
            return;

        for (int i = 0; i < 6; i++) {
            int x = dragon.level.rand.nextInt(256) - 128;
            int z = dragon.level.rand.nextInt(256) - 128;
            BlockPos pos = new BlockPos(x, dragon.level.getHeight(Heightmap.Type.WORLD_SURFACE, x, z), z);
            if (!dragon.level.isBlockLoaded(pos))
                continue;
            if (dragon.level.getBlockState(pos.down()).getBlock() != Blocks.END_STONE)
                continue;
            dragon.level.setBlockState(pos, this.getDefaultState());
        }
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader levelIn, BlockPos pos) {
        return state.getBlock() == Blocks.END_STONE;
    }

    @Override
    public boolean isValidPosition(BlockState state, ILevelReader levelIn, BlockPos pos) {
        return levelIn.getBlockState(pos.down()).getBlock() == Blocks.END_STONE;
    }

    @Override
    public String getBaseName() {
        return "end_flower";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader level) {
        return new BlockEntityEndFlower();
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return willHarvest || super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void harvestBlock(Level levelIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.harvestBlock(levelIn, player, pos, state, te, stack);
        levelIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity tile = builder.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof BlockEntityEndFlower && ((BlockEntityEndFlower) tile).isDrainMode)
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

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutout;
    }
}
