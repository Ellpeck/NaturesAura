package de.ellpeck.naturesaura.blocks;

import com.mojang.serialization.MapCodec;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;

public class BlockDimensionRail extends BaseRailBlock implements IModItem, ICustomBlockState, ICustomItemModel {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    private final String name;
    private final ResourceKey<Level> goalDim;
    private final ResourceKey<Level>[] canUseDims;

    @SafeVarargs
    public BlockDimensionRail(String name, ResourceKey<Level> goalDim, ResourceKey<Level>... canUseDims) {
        super(false, Properties.ofFullCopy(Blocks.RAIL));
        this.name = name;
        this.goalDim = goalDim;
        this.canUseDims = canUseDims;

        ModRegistry.ALL_ITEMS.add(this);
    }

    private boolean canUseHere(ResourceKey<Level> dimension) {
        for (var dim : this.canUseDims)
            if (dim == dimension)
                return true;
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!level.isClientSide) {
                var goalPos = this.getGoalCoords(level, pos);
                var data = new CompoundTag();
                data.putString("dim", this.goalDim.location().toString());
                data.putLong("pos", goalPos.asLong());
                PacketHandler.sendTo(player, new PacketClient(0, data));
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (level.isClientSide)
            return;
        if (!cart.getPassengers().isEmpty())
            return;
        if (!this.canUseHere(level.dimension()))
            return;

        var box = cart.getBoundingBox();
        PacketHandler.sendToAllAround(level, pos, 32, new PacketParticles((float) box.minX, (float) box.minY, (float) box.minZ, PacketParticles.Type.DIMENSION_RAIL, (int) ((box.maxX - box.minX) * 100F), (int) ((box.maxY - box.minY) * 100F), (int) ((box.maxZ - box.minZ) * 100F)));
        level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1F, 1F);

        var goalCoords = this.getGoalCoords(level, pos).getCenter();
        cart.changeDimension(new DimensionTransition(level.getServer().getLevel(this.goalDim), goalCoords, cart.getDeltaMovement(), cart.getYRot(), cart.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));

        var spot = IAuraChunk.getHighestSpot(level, pos, 35, pos);
        IAuraChunk.getAuraChunk(level, spot).drainAura(spot, 50000);
    }

    private BlockPos getGoalCoords(Level level, BlockPos pos) {
        var server = level.getServer();
        if (this == ModBlocks.DIMENSION_RAIL_NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (this == ModBlocks.DIMENSION_RAIL_END) {
            // travel to the end from the overworld
            return ServerLevel.END_SPAWN_POINT.above(8);
        } else {
            if (level.dimension() == Level.OVERWORLD) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                var overworld = server.getLevel(this.goalDim);
                var spawn = overworld.getSharedSpawnPos();
                var ret = new BlockPos(spawn.getX(), 0, spawn.getZ());
                return ret.above(overworld.getHeight(Heightmap.Types.WORLD_SURFACE, spawn.getX(), spawn.getZ()));
            }
        }
    }

    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return null;
    }

    @Override
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    public Property<RailShape> getShapeProperty() {
        return BlockDimensionRail.SHAPE;
    }

    @Override
    public boolean isFlexibleRail(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockDimensionRail.SHAPE, BaseRailBlock.WATERLOGGED);
    }

    @Override
    public String getBaseName() {
        return "dimension_rail_" + this.name;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        // noop
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "block/" + this.getBaseName());
    }

}
