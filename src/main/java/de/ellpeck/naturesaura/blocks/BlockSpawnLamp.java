package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySpawnLamp;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockSpawnLamp extends BlockContainerImpl implements IVisualizable {

    // TODO bounding box
    private static final AxisAlignedBB AABB = new AxisAlignedBB(4 / 16F, 0F, 4 / 16F, 12 / 16F, 13 / 16F, 12 / 16F);

    public BlockSpawnLamp() {
        super("spawn_lamp", TileEntitySpawnLamp::new, ModBlocks.prop(Material.IRON).hardnessAndResistance(3F).lightValue(15).sound(SoundType.METAL));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawner() != null)
            return;
        IWorld world = event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        Helper.getTileEntitiesInArea(world, pos, 48, tile -> {
            if (!(tile instanceof TileEntitySpawnLamp))
                return false;
            TileEntitySpawnLamp lamp = (TileEntitySpawnLamp) tile;
            int range = lamp.getRadius();
            if (range <= 0)
                return false;

            BlockPos lampPos = lamp.getPos();
            if (!new AxisAlignedBB(lampPos).grow(range).contains(new Vec3d(pos)))
                return false;

            MobEntity entity = (MobEntity) event.getEntityLiving();
            if (entity.canSpawn(world, event.getSpawnReason()) && entity.isNotColliding(world)) {
                BlockPos spot = IAuraChunk.getHighestSpot(world, lampPos, 32, lampPos);
                IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 200);

                PacketHandler.sendToAllAround(world, lampPos, 32,
                        new PacketParticles(lampPos.getX(), lampPos.getY(), lampPos.getZ(), 15));
            }

            event.setResult(Event.Result.DENY);
            return true;
        });
    }

/*    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IWorld source, BlockPos pos) {
        return AABB;
    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

/*    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(BlockState baseState, IBlockAccess world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySpawnLamp) {
            int radius = ((TileEntitySpawnLamp) tile).getRadius();
            if (radius > 0)
                return new AxisAlignedBB(pos).grow(radius);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x825ee5;
    }
}
