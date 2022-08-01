package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEndFlower;
import de.ellpeck.naturesaura.blocks.tiles.ITickableBlockEntity;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEndFlower extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel, EntityBlock {

    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public BlockEndFlower() {
        super(Properties.of(Material.GRASS).noCollission().strength(0.5F).sound(SoundType.GRASS));
        MinecraftForge.EVENT_BUS.register(this);
        ModRegistry.ALL_ITEMS.add(this);
        ModRegistry.ALL_ITEMS.add(new ModTileType<BlockEntityEndFlower>(BlockEntityEndFlower::new, this));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        var vec3d = state.getOffset(levelIn, pos);
        return BlockEndFlower.SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @SubscribeEvent
    public void onDragonTick(LivingEvent.LivingTickEvent event) {
        var living = event.getEntity();
        if (living.level.isClientSide || !(living instanceof EnderDragon dragon))
            return;
        if (dragon.dragonDeathTime < 150 || dragon.dragonDeathTime % 10 != 0)
            return;

        for (var i = 0; i < 6; i++) {
            var x = dragon.level.random.nextInt(256) - 128;
            var z = dragon.level.random.nextInt(256) - 128;
            var pos = new BlockPos(x, dragon.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
            if (!dragon.level.isLoaded(pos))
                continue;
            if (dragon.level.getBlockState(pos.below()).getBlock() != Blocks.END_STONE)
                continue;
            dragon.level.setBlockAndUpdate(pos, this.defaultBlockState());
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelIn, BlockPos pos) {
        return levelIn.getBlockState(pos.below()).getBlock() == Blocks.END_STONE;
    }

    @Override
    public String getBaseName() {
        return "end_flower";
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityEndFlower(pos, state);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ITickableBlockEntity.createTickerHelper(type, ModBlockEntities.END_FLOWER);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return willHarvest || super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void playerDestroy(Level levelIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(levelIn, player, pos, state, te, stack);
        levelIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        var tile = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof BlockEntityEndFlower && ((BlockEntityEndFlower) tile).isDrainMode)
            return NonNullList.create();
        return super.getDrops(state, builder);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cross(this.getBaseName(), generator.modLoc("block/" + this.getBaseName())).renderType("cutout"));
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "block/" + this.getBaseName());
    }
}
