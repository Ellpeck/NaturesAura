package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class BlockDimensionRail extends BlockRailBase implements IModItem, ICreativeItem, IModelProvider {

    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", EnumRailDirection.class, EnumRailDirection.NORTH_SOUTH, EnumRailDirection.EAST_WEST);

    private final String name;
    private final int goalDim;
    private final DimensionType[] canUseDims;

    public BlockDimensionRail(String name, DimensionType goalDim, DimensionType... canUseDims) {
        super(false);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        if (world.isRemote)
            return;
        if (cart.isBeingRidden())
            return;
        if (!this.canUseHere(world.provider.getDimensionType()))
            return;

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
            WorldServer end = server.getWorld(this.goalDim);
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SHAPE).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
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
