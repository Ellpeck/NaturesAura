package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class BlockDimensionRail extends AbstractRailBlock implements IModItem, ICreativeItem, IModelProvider {

    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", EnumRailDirection.class, EnumRailDirection.NORTH_SOUTH, EnumRailDirection.EAST_WEST);

    private final String name;
    private final int goalDim;
    private final DimensionType[] canUseDims;

    public BlockDimensionRail(String name, DimensionType goalDim, DimensionType... canUseDims) {
        super(false);
        this.name = name;
        this.goalDim = goalDim.getId();
        this.canUseDims = canUseDims;
        this.setHardness(0.8F);
        this.setSoundType(SoundType.METAL);

        ModRegistry.add(this);
    }

    private boolean canUseHere(DimensionType dimension) {
        for (DimensionType dim : this.canUseDims)
            if (dim == dimension)
                return true;
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!worldIn.isRemote) {
                BlockPos goalPos = this.getGoalCoords(worldIn, pos);
                PacketHandler.sendTo(playerIn,
                        new PacketClient(0, this.goalDim, goalPos.getX(), goalPos.getY(), goalPos.getZ()));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMinecartPass(World world, AbstractMinecartEntity cart, BlockPos pos) {
        if (world.isRemote)
            return;
        if (cart.isBeingRidden())
            return;
        if (!this.canUseHere(world.provider.getDimensionType()))
            return;

        AxisAlignedBB box = cart.getEntityBoundingBox();
        PacketHandler.sendToAllAround(world, pos, 32,
                new PacketParticles((float) box.minX, (float) box.minY, (float) box.minZ, 25,
                        (int) ((box.maxX - box.minX) * 100F), (int) ((box.maxY - box.minY) * 100F), (int) ((box.maxZ - box.minZ) * 100F)));
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);

        BlockPos goalCoords = this.getGoalCoords(world, pos);
        cart.changeDimension(this.goalDim, (newWorld, entity, yaw) ->
                entity.moveToBlockPosAndAngles(goalCoords, yaw, entity.rotationPitch));

        BlockPos spot = IAuraChunk.getHighestSpot(world, pos, 35, pos);
        IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 50000);
    }

    private BlockPos getGoalCoords(World world, BlockPos pos) {
        MinecraftServer server = world.getMinecraftServer();
        if (this == ModBlocks.DIMENSION_RAIL_NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (this == ModBlocks.DIMENSION_RAIL_END) {
            // travel to the end from the overworld
            ServerWorld end = server.getWorld(this.goalDim);
            return end.getSpawnCoordinate().up(8);
        } else {
            if (world.provider.getDimensionType() == DimensionType.NETHER) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                World overworld = server.getWorld(this.goalDim);
                return overworld.getTopSolidOrLiquidBlock(overworld.getSpawnPoint());
            }
        }
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHAPE);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(SHAPE).getMetadata();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SHAPE, EnumRailDirection.byMetadata(meta));
    }

    @Override
    public String getBaseName() {
        return "dimension_rail_" + this.name;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }
}
