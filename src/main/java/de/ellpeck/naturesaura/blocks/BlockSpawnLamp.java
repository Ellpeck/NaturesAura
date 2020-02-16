package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySpawnLamp;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public class BlockSpawnLamp extends BlockContainerImpl implements IVisualizable, ICustomBlockState, ICustomRenderType {

    private static final VoxelShape SHAPE = VoxelShapes.create(4 / 16F, 0F, 4 / 16F, 12 / 16F, 13 / 16F, 12 / 16F);

    public BlockSpawnLamp() {
        super("spawn_lamp", TileEntitySpawnLamp::new, ModBlocks.prop(Material.IRON).hardnessAndResistance(3F).lightValue(15).sound(SoundType.METAL));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawner() != null)
            return;
        IWorld world = event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!(world instanceof World))
            return;
        WorldData data = (WorldData) IWorldData.getWorldData((World) world);
        for (TileEntitySpawnLamp lamp : data.spawnLamps) {
            int range = lamp.getRadius();
            if (range <= 0)
                continue;

            BlockPos lampPos = lamp.getPos();
            if (!new AxisAlignedBB(lampPos).grow(range).contains(new Vec3d(pos)))
                continue;

            MobEntity entity = (MobEntity) event.getEntityLiving();
            if (entity.canSpawn(world, event.getSpawnReason()) && entity.isNotColliding(world)) {
                BlockPos spot = IAuraChunk.getHighestSpot(world, lampPos, 32, lampPos);
                IAuraChunk.getAuraChunk(world, spot).drainAura(spot, 200);

                PacketHandler.sendToAllAround(world, lampPos, 32,
                        new PacketParticles(lampPos.getX(), lampPos.getY(), lampPos.getZ(), PacketParticles.Type.SPAWN_LAMP));
            }

            event.setResult(Event.Result.DENY);
            break;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

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

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::cutoutMipped;
    }
}
