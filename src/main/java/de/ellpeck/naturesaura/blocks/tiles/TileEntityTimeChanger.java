package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

import java.util.List;

public class TileEntityTimeChanger extends TileEntityImpl implements ITickable {

    private long goalTime;

    @Override
    public void update() {
        if (!this.world.isRemote) {
            List<EntityItemFrame> frames = Helper.getAttachedItemFrames(this.world, this.pos);
            for (EntityItemFrame frame : frames) {
                ItemStack frameStack = frame.getDisplayedItem();
                if (frameStack.isEmpty() || frameStack.getItem() != ModItems.CLOCK_HAND)
                    continue;

                if (this.goalTime > 0) {
                    long current = this.world.getWorldTime();
                    long toAdd = Math.min(75, this.goalTime - current);
                    if (toAdd <= 0) {
                        this.goalTime = 0;
                        this.sendToClients();
                        return;
                    }
                    this.world.setWorldTime(current + toAdd);

                    BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
                    IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, (int) toAdd * 20);

                    if (this.world instanceof WorldServer) {
                        PlayerList list = this.world.getMinecraftServer().getPlayerList();
                        list.sendPacketToAllPlayersInDimension(new SPacketTimeUpdate(
                                this.world.getTotalWorldTime(), this.world.getWorldTime(),
                                this.world.getGameRules().getBoolean("doDaylightCycle")), this.world.provider.getDimension());
                    }
                    return;
                }

                if (this.world.getTotalWorldTime() % 20 != 0)
                    return;

                List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class,
                        new AxisAlignedBB(this.pos).grow(1), EntitySelectors.IS_ALIVE);
                for (EntityItem item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.CLOCK)
                        continue;

                    int dayGoal = MathHelper.floor((frame.getRotation() / 8F) * 24000F) + 18000;
                    long current = this.world.getWorldTime();
                    long toMove = (24000 - current % 24000 + dayGoal) % 24000;
                    this.goalTime = current + toMove;
                    this.sendToClients();

                    if (stack.getCount() <= 1)
                        item.setDead();
                    else {
                        stack.shrink(1);
                        item.setItem(stack);
                    }
                    return;
                }
            }
            if (this.goalTime > 0) {
                this.goalTime = 0;
                this.sendToClients();
            }
        } else if (this.goalTime > 0) {
            if (this.world.getTotalWorldTime() % 5 == 0)
                NaturesAuraAPI.instance().spawnParticleStream(
                        this.pos.getX() + (float) this.world.rand.nextGaussian() * 5F,
                        this.pos.getY() + 1 + this.world.rand.nextFloat() * 5F,
                        this.pos.getZ() + (float) this.world.rand.nextGaussian() * 5F,
                        this.pos.getX() + this.world.rand.nextFloat(),
                        this.pos.getY() + this.world.rand.nextFloat(),
                        this.pos.getZ() + this.world.rand.nextFloat(),
                        this.world.rand.nextFloat() * 0.07F + 0.07F, IAuraType.forWorld(this.world).getColor(), this.world.rand.nextFloat() + 0.5F);

            if (this.world.rand.nextFloat() >= 0.25F) {
                int color = this.goalTime % 24000 > 12000 ? 0xe2e2e2 : 0xffe926;
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.pos.getX() + this.world.rand.nextFloat(),
                        this.pos.getY() + 1,
                        this.pos.getZ() + this.world.rand.nextFloat(),
                        -0.05F - this.world.rand.nextFloat() * 0.02F,
                        this.world.rand.nextFloat() * 0.25F,
                        this.world.rand.nextGaussian() * 0.02F,
                        color, 1F + this.world.rand.nextFloat() * 2F,
                        this.world.rand.nextInt(100) + 100, 0, true, true);
            }
        }
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            compound.setLong("goal", this.goalTime);
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.goalTime = compound.getLong("goal");
    }
}
