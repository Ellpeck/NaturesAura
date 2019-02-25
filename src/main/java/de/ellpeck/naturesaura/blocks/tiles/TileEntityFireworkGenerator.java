package de.ellpeck.naturesaura.blocks.tiles;

import com.google.common.primitives.Ints;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntityFireworkGenerator extends TileEntityImpl implements ITickable {

    private EntityFireworkRocket trackedEntity;
    private ItemStack trackedItem;
    private int toRelease;
    private int releaseTimer;

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 10 == 0) {
                List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class,
                        new AxisAlignedBB(this.pos).grow(4), EntitySelectors.IS_ALIVE);
                for (EntityItem item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.FIREWORKS)
                        continue;
                    if (this.trackedEntity == null && this.releaseTimer <= 0) {
                        EntityFireworkRocket entity = new EntityFireworkRocket(this.world, item.posX, item.posY, item.posZ, stack);
                        this.trackedEntity = entity;
                        this.trackedItem = stack;
                        this.world.spawnEntity(entity);
                    }
                    stack.shrink(1);
                    if (stack.isEmpty())
                        item.setDead();
                    else
                        item.setItem(stack);
                }
            }

            if (this.trackedEntity != null && this.trackedEntity.isDead) {
                if (this.trackedItem.hasTagCompound()) {
                    float generateFactor = 0;
                    Set<Integer> usedColors = new HashSet<>();

                    NBTTagCompound compound = this.trackedItem.getTagCompound();
                    NBTTagCompound fireworks = compound.getCompoundTag("Fireworks");

                    int flightTime = fireworks.getInteger("Flight");
                    NBTTagList explosions = fireworks.getTagList("Explosions", 10);
                    if (!explosions.isEmpty()) {
                        generateFactor += flightTime;

                        for (NBTBase base : explosions) {
                            NBTTagCompound explosion = (NBTTagCompound) base;
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
                                (float) this.trackedEntity.posX, (float) this.trackedEntity.posY, (float) this.trackedEntity.posZ,
                                24, Ints.toArray(data)));
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
                            new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 8));
                }
            }
        }
    }
}
