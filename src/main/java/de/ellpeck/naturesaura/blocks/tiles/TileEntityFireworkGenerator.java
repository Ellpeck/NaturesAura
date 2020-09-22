package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.primitives.Ints;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntityFireworkGenerator extends TileEntityImpl implements ITickableTileEntity {

    private FireworkRocketEntity trackedEntity;
    private ItemStack trackedItem;
    private int toRelease;
    private int releaseTimer;

    public TileEntityFireworkGenerator() {
        super(ModTileEntities.FIREWORK_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 10 == 0) {
                List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.pos).grow(4), EntityPredicates.IS_ALIVE);
                for (ItemEntity item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.FIREWORK_ROCKET)
                        continue;
                    if (this.trackedEntity == null && this.releaseTimer <= 0) {
                        FireworkRocketEntity entity = new FireworkRocketEntity(this.world, item.getPosX(), item.getPosY(), item.getPosZ(), stack);
                        this.trackedEntity = entity;
                        this.trackedItem = stack.copy();
                        this.world.addEntity(entity);
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

                    CompoundNBT compound = this.trackedItem.getTag();
                    CompoundNBT fireworks = compound.getCompound("Fireworks");

                    int flightTime = fireworks.getInt("Flight");
                    ListNBT explosions = fireworks.getList("Explosions", 10);
                    if (!explosions.isEmpty()) {
                        generateFactor += flightTime;

                        for (INBT base : explosions) {
                            CompoundNBT explosion = (CompoundNBT) base;
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
                        int toAdd = MathHelper.ceil(generateFactor * 10000F);
                        if (this.canGenerateRightNow(35, toAdd)) {
                            this.toRelease = toAdd;
                            this.releaseTimer = 15 * flightTime + 40;
                        }

                        List<Integer> data = new ArrayList<>();
                        data.add(this.pos.getX());
                        data.add(this.pos.getY());
                        data.add(this.pos.getZ());
                        data.addAll(usedColors);
                        PacketHandler.sendToAllLoaded(this.world, this.pos, new PacketParticles(
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
                    while (this.toRelease > 0) {
                        BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 35, this.pos);
                        this.toRelease -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, this.toRelease);
                    }

                    PacketHandler.sendToAllLoaded(this.world, this.pos,
                            new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION));
                }
            }
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
