package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraft.level.gen.Heightmap;
import net.minecraft.level.server.ServerLevel;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDimensionRail extends AbstractRailBlock implements IModItem, ICustomRenderType, ICustomBlockState, ICustomItemModel {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    private final String name;
    private final RegistryKey<Level> goalDim;
    private final RegistryKey<Level>[] canUseDims;

    public BlockDimensionRail(String name, RegistryKey<Level> goalDim, RegistryKey<Level>... canUseDims) {
        super(false, Properties.from(Blocks.RAIL));
        this.name = name;
        this.goalDim = goalDim;
        this.canUseDims = canUseDims;

        ModRegistry.add(this);
    }

    private boolean canUseHere(RegistryKey<Level> dimension) {
        for (RegistryKey<Level> dim : this.canUseDims)
            if (dim == dimension)
                return true;
        return false;
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!levelIn.isClientSide) {
                BlockPos goalPos = this.getGoalCoords(levelIn, pos);
                CompoundTag data = new CompoundTag();
                data.putString("dim", this.goalDim.func_240901_a_().toString());
                data.putLong("pos", goalPos.toLong());
                PacketHandler.sendTo(player, new PacketClient(0, data));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecartEntity cart) {
        if (level.isClientSide)
            return;
        if (cart.isBeingRidden())
            return;
        if (!this.canUseHere(level.func_234923_W_()))
            return;

        AxisAlignedBB box = cart.getBoundingBox();
        PacketHandler.sendToAllAround(level, pos, 32, new PacketParticles((float) box.minX, (float) box.minY, (float) box.minZ, PacketParticles.Type.DIMENSION_RAIL, (int) ((box.maxX - box.minX) * 100F), (int) ((box.maxY - box.minY) * 100F), (int) ((box.maxZ - box.minZ) * 100F)));
        level.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);

        BlockPos goalCoords = this.getGoalCoords(level, pos);
        cart.changeDimension(level.getServer().getLevel(this.goalDim), new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destLevel, float yaw, Function<Boolean, Entity> repositionEntity) {
                // repositionEntity always causes a NPE because why wouldn't it, so this is a fixed copy
                entity.level.getProfiler().endStartSection("reloading");
                Entity result = entity.getType().create(destLevel);
                if (result != null) {
                    result.copyDataFromOld(entity);
                    destLevel.addFromAnotherDimension(result);
                    result.moveToBlockPosAndAngles(goalCoords, yaw, result.rotationPitch);
                }
                return result;
            }
        });

        BlockPos spot = IAuraChunk.getHighestSpot(level, pos, 35, pos);
        IAuraChunk.getAuraChunk(level, spot).drainAura(spot, 50000);
    }

    private BlockPos getGoalCoords(Level level, BlockPos pos) {
        MinecraftServer server = level.getServer();
        if (this == ModBlocks.DIMENSION_RAIL_NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (this == ModBlocks.DIMENSION_RAIL_END) {
            // travel to the end from the overworld
            return ServerLevel.field_241108_a_.up(8);
        } else {
            if (level.func_234923_W_() == Level.field_234919_h_) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                ServerLevel overworld = server.getLevel(this.goalDim);
                BlockPos spawn = overworld.func_241135_u_();
                BlockPos ret = new BlockPos(spawn.getX(), 0, spawn.getZ());
                return ret.up(overworld.getHeight(Heightmap.Type.WORLD_SURFACE, spawn.getX(), spawn.getZ()));
            }
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean isFlexibleRail(BlockState state, IBlockReader level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader level, BlockPos pos) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
    }

    @Override
    public String getBaseName() {
        return "dimension_rail_" + this.name;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
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
