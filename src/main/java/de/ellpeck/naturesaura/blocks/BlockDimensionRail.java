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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockDimensionRail extends AbstractRailBlock implements IModItem, ICustomRenderType, ICustomBlockState, ICustomItemModel {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    private final String name;
    private final int goalDim;
    private final DimensionType[] canUseDims;

    public BlockDimensionRail(String name, DimensionType goalDim, DimensionType... canUseDims) {
        super(false, ModBlocks.prop(Blocks.RAIL));
        this.name = name;
        this.goalDim = goalDim.getId();
        this.canUseDims = canUseDims;

        ModRegistry.add(this);
    }

    private boolean canUseHere(DimensionType dimension) {
        for (DimensionType dim : this.canUseDims)
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
                PacketHandler.sendTo(player, new PacketClient(0, this.goalDim, goalPos.getX(), goalPos.getY(), goalPos.getZ()));
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
        if (!this.canUseHere(world.getDimension().getType()))
            return;

        AxisAlignedBB box = cart.getBoundingBox();
        PacketHandler.sendToAllAround(world, pos, 32, new PacketParticles((float) box.minX, (float) box.minY, (float) box.minZ, PacketParticles.Type.DIMENSION_RAIL, (int) ((box.maxX - box.minX) * 100F), (int) ((box.maxY - box.minY) * 100F), (int) ((box.maxZ - box.minZ) * 100F)));
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);

        BlockPos goalCoords = this.getGoalCoords(world, pos);

        // TODO wait for Forge to have re-implemented ITeleporter
        if (true)
            return;

        cart.changeDimension(DimensionType.getById(this.goalDim));
        cart.moveToBlockPosAndAngles(goalCoords, cart.rotationYaw, cart.rotationPitch);

        BlockPos spot = IAuraChunk.getHighestSpot(world, pos, 35, pos);
        IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 50000);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("CURRENTLY UNIMPLEMENTED - Waiting for a Forge change").setStyle(new Style().setColor(TextFormatting.RED)));
    }

    private BlockPos getGoalCoords(World world, BlockPos pos) {
        MinecraftServer server = world.getServer();
        DimensionType goalDimType = DimensionType.getById(this.goalDim);
        if (this == ModBlocks.DIMENSION_RAIL_NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (this == ModBlocks.DIMENSION_RAIL_END) {
            // travel to the end from the overworld
            ServerWorld end = server.getWorld(goalDimType);
            return end.getSpawnCoordinate().up(8);
        } else {
            if (world.getDimension().getType() == DimensionType.THE_NETHER) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                World overworld = server.getWorld(goalDimType);
                BlockPos spawn = overworld.getSpawnPoint();
                BlockPos ret = new BlockPos(spawn.getX(), 0, spawn.getZ());
                return ret.up(overworld.getHeight(Heightmap.Type.WORLD_SURFACE_WG, spawn.getX(), spawn.getZ()));
            }
        }
    }

    @Override
    public IProperty<RailShape> getShapeProperty() {
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
