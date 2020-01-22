package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class TileEntityTimeChanger extends TileEntityImpl implements ITickableTileEntity {

    private long goalTime;

    public TileEntityTimeChanger() {
        super(ModTileEntities.TIME_CHANGER);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.world, this.pos);
            for (ItemFrameEntity frame : frames) {
                ItemStack frameStack = frame.getDisplayedItem();
                if (frameStack.isEmpty() || frameStack.getItem() != ModItems.CLOCK_HAND)
                    continue;

                if (this.goalTime > 0) {
                    long current = this.world.getDayTime();
                    long toAdd = Math.min(75, this.goalTime - current);
                    if (toAdd <= 0) {
                        this.goalTime = 0;
                        this.sendToClients();
                        return;
                    }
                    this.world.setDayTime(current + toAdd);

                    BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
                    IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, (int) toAdd * 20);

                    if (this.world instanceof ServerWorld) {
                        PlayerList list = this.world.getServer().getPlayerList();
                        list.sendPacketToAllPlayersInDimension(new SUpdateTimePacket(
                                this.world.getGameTime(), this.world.getDayTime(),
                                this.world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), this.world.getDimension().getType());
                    }
                    return;
                }

                if (this.world.getGameTime() % 20 != 0)
                    return;

                List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.pos).grow(1), EntityPredicates.IS_ALIVE);
                for (ItemEntity item : items) {
                    if (item.cannotPickup())
                        continue;
                    ItemStack stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.CLOCK)
                        continue;

                    int dayGoal = MathHelper.floor((frame.getRotation() / 8F) * 24000F) + 18000;
                    long current = this.world.getDayTime();
                    long toMove = (24000 - current % 24000 + dayGoal) % 24000;
                    this.goalTime = current + toMove;
                    this.sendToClients();

                    if (stack.getCount() <= 1)
                        item.remove();
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
        } else if (this.goalTime > 0 && this.world.rand.nextFloat() >= 0.25F) {
            double angle = Math.toRadians(this.world.getDayTime() * 5F % 360);
            double x = this.pos.getX() + 0.5 + Math.sin(angle) * 3F;
            double z = this.pos.getZ() + 0.5 + Math.cos(angle) * 3F;
            int color = this.goalTime % 24000 > 12000 ? 0xe2e2e2 : 0xffe926;
            NaturesAuraAPI.instance().spawnMagicParticle(
                    x, this.pos.getY() + 0.1F, z,
                    0F, 0.12F, 0F,
                    color, 1F + this.world.rand.nextFloat() * 2F,
                    this.world.rand.nextInt(100) + 100, 0, false, true);
            NaturesAuraAPI.instance().spawnMagicParticle(
                    x, this.pos.getY() + 0.1F, z,
                    0F, 0F, 0F,
                    IAuraType.forWorld(this.world).getColor(), 1F + this.world.rand.nextFloat(),
                    150, 0, false, true);
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            compound.putLong("goal", this.goalTime);
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.goalTime = compound.getLong("goal");
    }
}
