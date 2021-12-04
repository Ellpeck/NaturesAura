package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.primitives.Ints;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEntityFireworkGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private FireworkRocketEntity trackedEntity;
    private ItemStack trackedItem;
    private int toRelease;
    private int releaseTimer;

    public BlockEntityFireworkGenerator() {
        super(ModTileEntities.FIREWORK_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 == 0) {
                List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.worldPosition).grow(4), EntityPredicates.IS_ALIVE);
                for (ItemEntity item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.FIREWORK_ROCKET)
                        continue;
                    if (this.trackedEntity == null && this.releaseTimer <= 0) {
                        FireworkRocketEntity entity = new FireworkRocketEntity(this.level, item.getPosX(), item.getPosY(), item.getPosZ(), stack);
                        this.trackedEntity = entity;
                        this.trackedItem = stack.copy();
                        this.level.addEntity(entity);
                    }
                    stack.shrink(1);
                    if (stack.isEmpty())
                        item.remove();
                    else
                        item.setItem(stack);
                }
            }

            if (this.trackedEntity != null && !this.trackedEntity.isAlive()) {
                if (this.trackedItem.hasTag()) {
                    float generateFactor = 0;
                    Set<Integer> usedColors = new HashSet<>();

                    CompoundTag compound = this.trackedItem.getTag();
                    CompoundTag fireworks = compound.getCompound("Fireworks");

                    int flightTime = fireworks.getInt("Flight");
                    ListNBT explosions = fireworks.getList("Explosions", 10);
                    if (!explosions.isEmpty()) {
                        generateFactor += flightTime;

                        for (INBT base : explosions) {
                            CompoundTag explosion = (CompoundTag) base;
                            generateFactor += 1.5F;

                            boolean flicker = explosion.getBoolean("Flicker");
                            if (flicker)
                                generateFactor += 1;

                            boolean trail = explosion.getBoolean("Trail");
                            if (trail)
                                generateFactor += 8;

                            byte type = explosion.getByte("Type");
                            generateFactor += new float[]{0, 1, 0.5F, 20, 0.5F}[type];

                            Set<Integer> colors = new HashSet<>();
                            for (int color : explosion.getIntArray("Colors")) {
                                usedColors.add(color);
                                colors.add(color);
                            }
                            generateFactor += 0.75F * colors.size();
                        }
                    }

                    if (generateFactor > 0) {
                        int toAdd = Mth.ceil(generateFactor * 10000F);
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
                                (float) this.trackedEntity.getPosX(), (float) this.trackedEntity.getPosY(), (float) this.trackedEntity.getPosZ(),
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
