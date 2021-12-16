package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDimensionRail extends BaseRailBlock implements IModItem, ICustomRenderType, ICustomBlockState, ICustomItemModel {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    private final String name;
    private final ResourceKey<Level> goalDim;
    private final ResourceKey<Level>[] canUseDims;

    @SafeVarargs
    public BlockDimensionRail(String name, ResourceKey<Level> goalDim, ResourceKey<Level>... canUseDims) {
        super(false, Properties.copy(Blocks.RAIL));
        this.name = name;
        this.goalDim = goalDim;
        this.canUseDims = canUseDims;

        ModRegistry.add(this);
    }

    private boolean canUseHere(ResourceKey<Level> dimension) {
        for (var dim : this.canUseDims)
            if (dim == dimension)
                return true;
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var stack = player.getItemInHand(hand);
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!levelIn.isClientSide) {
                var goalPos = this.getGoalCoords(levelIn, pos);
                var data = new CompoundTag();
                data.putString("dim", this.goalDim.location().toString());
                data.putLong("pos", goalPos.asLong());
                PacketHandler.sendTo(player, new PacketClient(0, data));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
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

        var goalCoords = this.getGoalCoords(level, pos);
        cart.changeDimension(level.getServer().getLevel(this.goalDim), new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destLevel, float yaw, Function<Boolean, Entity> repositionEntity) {
                // repositionEntity always causes a NPE because why wouldn't it, so this is a fixed copy
                entity.level.getProfiler().popPush("reloading");
                var result = entity.getType().create(destLevel);
                if (result != null) {
                    result.restoreFrom(entity);
                    destLevel.addDuringTeleport(result);
                    result.moveTo(goalCoords, yaw, result.getXRot());
                }
                return result;
            }
        });

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
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
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
        builder.add(SHAPE, WATERLOGGED);
    }

    @Override
    public String getBaseName() {
        return "dimension_rail_" + this.name;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::cutoutMipped;
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
