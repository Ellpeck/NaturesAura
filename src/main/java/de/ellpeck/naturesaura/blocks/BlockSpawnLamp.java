package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySpawnLamp;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public class BlockSpawnLamp extends BlockContainerImpl implements IVisualizable, ICustomBlockState, ICustomRenderType {

    private static final VoxelShape SHAPE = Shapes.create(4 / 16F, 0F, 4 / 16F, 12 / 16F, 13 / 16F, 12 / 16F);

    public BlockSpawnLamp() {
        super("spawn_lamp", BlockEntitySpawnLamp::new, Properties.of(Material.METAL).strength(3F).lightLevel(s -> 15).sound(SoundType.METAL));
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
        LevelAccessor level = event.getWorld();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!(level instanceof Level))
            return;
        LevelData data = (LevelData) ILevelData.getLevelData((Level) level);
        for (BlockEntitySpawnLamp lamp : data.spawnLamps) {
            if (lamp.isRemoved())
                continue;

            int range = lamp.getRadius();
            if (range <= 0)
                continue;

            BlockPos lampPos = lamp.getBlockPos();
            if (!new AABB(lampPos).inflate(range).contains(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)))
                continue;

            Mob entity = (Mob) event.getEntityLiving();
            if (entity.checkSpawnRules(level, event.getSpawnReason()) && entity.checkSpawnObstruction(level)) {
                BlockPos spot = IAuraChunk.getHighestSpot((Level) level, lampPos, 32, lampPos);
                IAuraChunk.getAuraChunk((Level) level, spot).drainAura(spot, 200);

                PacketHandler.sendToAllAround((ServerLevel) level, lampPos, 32,
                        new PacketParticles(lampPos.getX(), lampPos.getY(), lampPos.getZ(), PacketParticles.Type.SPAWN_LAMP));
            }

            event.setResult(Event.Result.DENY);
            break;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntitySpawnLamp) {
            int radius = ((BlockEntitySpawnLamp) tile).getRadius();
            if (radius > 0)
                return new AABB(pos).inflate(radius);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
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
