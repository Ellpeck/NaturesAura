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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDimensionRail extends AbstractRailBlock implements IModItem, ICustomRenderType, ICustomBlockState, ICustomItemModel {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    private final String name;
    private final RegistryKey<World> goalDim;
    private final RegistryKey<World>[] canUseDims;

    public BlockDimensionRail(String name, RegistryKey<World> goalDim, RegistryKey<World>... canUseDims) {
        super(false, ModBlocks.prop(Blocks.RAIL));
        this.name = name;
        this.goalDim = goalDim;
        this.canUseDims = canUseDims;

        ModRegistry.add(this);
    }

    private boolean canUseHere(RegistryKey<World> dimension) {
        for (RegistryKey<World> dim : this.canUseDims)
            if (dim == dimension)
                return true;
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!worldIn.isRemote) {
                BlockPos goalPos = this.getGoalCoords(worldIn, pos);
                CompoundNBT data = new CompoundNBT();
                data.putString("dim", this.goalDim.func_240901_a_().toString());
                data.putLong("pos", goalPos.toLong());
                PacketHandler.sendTo(player, new PacketClient(0, data));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        if (world.isRemote)
            return;
        if (cart.isBeingRidden())
            return;
        if (!this.canUseHere(world.func_234923_W_()))
            return;

        AxisAlignedBB box = cart.getBoundingBox();
        PacketHandler.sendToAllAround(world, pos, 32, new PacketParticles((float) box.minX, (float) box.minY, (float) box.minZ, PacketParticles.Type.DIMENSION_RAIL, (int) ((box.maxX - box.minX) * 100F), (int) ((box.maxY - box.minY) * 100F), (int) ((box.maxZ - box.minZ) * 100F)));
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);

        BlockPos goalCoords = this.getGoalCoords(world, pos);
        cart.changeDimension(world.getServer().getWorld(this.goalDim), new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                // repositionEntity always causes a NPE because why wouldn't it, so this is a fixed copy
                entity.world.getProfiler().endStartSection("reloading");
                Entity result = entity.getType().create(destWorld);
                if (result != null) {
                    result.copyDataFromOld(entity);
                    destWorld.addFromAnotherDimension(result);
                    result.moveToBlockPosAndAngles(goalCoords, yaw, result.rotationPitch);
                }
                return result;
            }
        });

        BlockPos spot = IAuraChunk.getHighestSpot(world, pos, 35, pos);
        IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 50000);
    }

    private BlockPos getGoalCoords(World world, BlockPos pos) {
        MinecraftServer server = world.getServer();
        if (this == ModBlocks.DIMENSION_RAIL_NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (this == ModBlocks.DIMENSION_RAIL_END) {
            // travel to the end from the overworld
            return ServerWorld.field_241108_a_.up(8);
        } else {
            if (world.func_234923_W_() == World.field_234919_h_) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                ServerWorld overworld = server.getWorld(this.goalDim);
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
    public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
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
