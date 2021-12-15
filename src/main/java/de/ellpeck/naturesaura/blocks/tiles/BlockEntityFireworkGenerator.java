package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.primitives.Ints;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEntityFireworkGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private FireworkRocketEntity trackedEntity;
    private ItemStack trackedItem;
    private int toRelease;
    private int releaseTimer;

    public BlockEntityFireworkGenerator(BlockPos pos, BlockState state) {
        super(ModTileEntities.FIREWORK_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 == 0) {
                var items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.worldPosition).inflate(4), Entity::isAlive);
                for (var item : items) {
                    if (item.hasPickUpDelay())
                        continue;
                    var stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.FIREWORK_ROCKET)
                        continue;
                    if (this.trackedEntity == null && this.releaseTimer <= 0) {
                        var entity = new FireworkRocketEntity(this.level, item.getX(), item.getY(), item.getZ(), stack);
                        this.trackedEntity = entity;
                        this.trackedItem = stack.copy();
                        this.level.addFreshEntity(entity);
                    }
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        item.kill();
                    } else {
                        item.setItem(stack);
                    }
                }
            }

            if (this.trackedEntity != null && !this.trackedEntity.isAlive()) {
                if (this.trackedItem.hasTag()) {
                    float generateFactor = 0;
                    Set<Integer> usedColors = new HashSet<>();

                    var compound = this.trackedItem.getTag();
                    var fireworks = compound.getCompound("Fireworks");

                    var flightTime = fireworks.getInt("Flight");
                    var explosions = fireworks.getList("Explosions", 10);
                    if (!explosions.isEmpty()) {
                        generateFactor += flightTime;

                        for (var base : explosions) {
                            var explosion = (CompoundTag) base;
                            generateFactor += 1.5F;

                            var flicker = explosion.getBoolean("Flicker");
                            if (flicker)
                                generateFactor += 1;

                            var trail = explosion.getBoolean("Trail");
                            if (trail)
                                generateFactor += 8;

                            var type = explosion.getByte("Type");
                            generateFactor += new float[]{0, 1, 0.5F, 20, 0.5F}[type];

                            Set<Integer> colors = new HashSet<>();
                            for (var color : explosion.getIntArray("Colors")) {
                                usedColors.add(color);
                                colors.add(color);
                            }
                            generateFactor += 0.75F * colors.size();
                        }
                    }

                    if (generateFactor > 0) {
                        var toAdd = Mth.ceil(generateFactor * 10000F);
                        if (this.canGenerateRightNow(toAdd)) {
                            this.toRelease = toAdd;
                            this.releaseTimer = 15 * flightTime + 40;
                        }

                        List<Integer> data = new ArrayList<>();
                        data.add(this.worldPosition.getX());
                        data.add(this.worldPosition.getY());
                        data.add(this.worldPosition.getZ());
                        data.addAll(usedColors);
                        PacketHandler.sendToAllLoaded(this.level, this.worldPosition, new PacketParticles(
                                (float) this.trackedEntity.getX(), (float) this.trackedEntity.getY(), (float) this.trackedEntity.getZ(),
                                PacketParticles.Type.FIREWORK_GEN, Ints.toArray(data)));
                    }
                }

                this.trackedEntity = null;
                this.trackedItem = null;
            }

            if (this.releaseTimer > 0) {
                this.releaseTimer--;
                if (this.releaseTimer <= 0) {
                    this.generateAura(this.toRelease);
                    this.toRelease = 0;

                    PacketHandler.sendToAllLoaded(this.level, this.worldPosition,
                            new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION));
                }
            }
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
