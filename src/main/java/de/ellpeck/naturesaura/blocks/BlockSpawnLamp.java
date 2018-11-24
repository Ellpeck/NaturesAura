package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySpawnLamp;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSpawnLamp extends BlockContainerImpl {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(4 / 16F, 0F, 4 / 16F, 12 / 16F, 13 / 16F, 12 / 16F);

    public BlockSpawnLamp() {
        super(Material.IRON, "spawn_lamp", TileEntitySpawnLamp.class, "spawn_lamp");
        MinecraftForge.EVENT_BUS.register(this);
        this.setLightLevel(1F);
    }

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawner() != null)
            return;
        World world = event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        Helper.getTileEntitiesInArea(world, pos, 48, tile -> {
            if (!(tile instanceof TileEntitySpawnLamp))
                return false;
            TileEntitySpawnLamp lamp = (TileEntitySpawnLamp) tile;
            int range = lamp.getRadius();
            if (range <= 0)
                return false;

            BlockPos lampPos = lamp.getPos();
            if (pos.distanceSq(lampPos.getX() + 0.5F, lampPos.getY() + 0.5F, lampPos.getZ() + 0.5F) > range * range)
                return false;

            EntityLiving entity = (EntityLiving) event.getEntityLiving();
            if (entity.getCanSpawnHere() && entity.isNotColliding()) {
                BlockPos spot = IAuraChunk.getHighestSpot(world, lampPos, 32, lampPos);
                IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 2);

                PacketHandler.sendToAllAround(world, lampPos, 32,
                        new PacketParticles(lampPos.getX(), lampPos.getY(), lampPos.getZ(), 15));
            }

            event.setResult(Event.Result.DENY);
            return true;
        });
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
