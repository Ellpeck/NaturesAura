package de.ellpeck.naturesaura.blocks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockDimensionRail extends BlockRailBase implements IModItem, ICreativeItem, IModelProvider {

    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", EnumRailDirection.class, EnumRailDirection.NORTH_SOUTH, EnumRailDirection.EAST_WEST);

    public BlockDimensionRail() {
        super(false);
        ModRegistry.add(this);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem() == ModItems.RANGE_VISUALIZER) {
            if (!worldIn.isRemote) {
                Type type = state.getValue(TYPE);
                BlockPos goalPos = this.getGoalCoords(worldIn, pos, type);
                PacketHandler.sendTo(playerIn,
                        new PacketClient(0, type.goalDim, goalPos.getX(), goalPos.getY(), goalPos.getZ()));
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
        IBlockState state = world.getBlockState(pos);
        Type type = state.getValue(TYPE);
        if (!type.canUseHere(world.provider.getDimensionType()))
            return;
        BlockPos goalCoords = this.getGoalCoords(world, pos, type);
        cart.changeDimension(type.goalDim, (newWorld, entity, yaw) ->
                entity.moveToBlockPosAndAngles(goalCoords, yaw, entity.rotationPitch));
    }

    private BlockPos getGoalCoords(World world, BlockPos pos, Type type) {
        MinecraftServer server = world.getMinecraftServer();
        if (type == Type.NETHER) {
            // travel to the nether from the overworld
            return new BlockPos(pos.getX() / 8, pos.getY() / 2, pos.getZ() / 8);
        } else if (type == Type.END) {
            // travel to the end from the overworld
            WorldServer end = server.getWorld(type.goalDim);
            return end.getSpawnCoordinate().up(8);
        } else {
            if (world.provider.getDimensionType() == DimensionType.NETHER) {
                // travel to the overworld from the nether
                return new BlockPos(pos.getX() * 8, pos.getY() * 2, pos.getZ() * 8);
            } else {
                // travel to the overworld from the end
                World overworld = server.getWorld(type.goalDim);
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
        return new BlockStateContainer(this, TYPE, SHAPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        meta |= state.getValue(SHAPE).getMetadata();
        meta |= state.getValue(TYPE).ordinal() << 1;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(SHAPE, EnumRailDirection.byMetadata(meta & 1))
                .withProperty(TYPE, Type.values()[meta >> 1]);
    }

    @Override
    public String getBaseName() {
        return "dimension_rail";
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

    public enum Type implements IStringSerializable {
        OVERWORLD("overworld", DimensionType.OVERWORLD, DimensionType.NETHER, DimensionType.THE_END),
        NETHER("nether", DimensionType.NETHER, DimensionType.OVERWORLD),
        END("end", DimensionType.THE_END, DimensionType.OVERWORLD);

        private final String name;
        private final int goalDim;
        private final DimensionType[] canUseDims;

        Type(String name, DimensionType goalDim, DimensionType... canUseDims) {
            this.name = name;
            this.goalDim = goalDim.getId();
            this.canUseDims = canUseDims;
        }

        public boolean canUseHere(DimensionType dimension) {
            for (DimensionType dim : this.canUseDims)
                if (dim == dimension)
                    return true;
            return false;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
