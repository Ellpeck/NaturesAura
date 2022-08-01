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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
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

public class BlockSpawnLamp extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    private static final VoxelShape SHAPE = Shapes.create(4 / 16F, 0F, 4 / 16F, 12 / 16F, 13 / 16F, 12 / 16F);

    public BlockSpawnLamp() {
        super("spawn_lamp", BlockEntitySpawnLamp.class, Properties.of(Material.METAL).strength(3F).lightLevel(s -> 15).sound(SoundType.METAL));
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
        var level = event.getLevel();
        var pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!(level instanceof Level))
            return;
        var data = (LevelData) ILevelData.getLevelData((Level) level);
        for (var lamp : data.spawnLamps) {
            if (lamp.isRemoved())
                continue;

            var range = lamp.getRadius();
            if (range <= 0)
                continue;

            var lampPos = lamp.getBlockPos();
            if (!new AABB(lampPos).inflate(range).contains(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)))
                continue;

            var entity = (Mob) event.getEntity();
            if (entity.checkSpawnRules(level, event.getSpawnReason()) && entity.checkSpawnObstruction(level)) {
                var spot = IAuraChunk.getHighestSpot((Level) level, lampPos, 32, lampPos);
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
        return BlockSpawnLamp.SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntitySpawnLamp) {
            var radius = ((BlockEntitySpawnLamp) tile).getRadius();
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
}
