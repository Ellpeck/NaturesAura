package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;

public class BlockEntityTimeChanger extends BlockEntityImpl implements ITickableBlockEntity {

    private long goalTime;

    public BlockEntityTimeChanger(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIME_CHANGER, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            var frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
            for (var frame : frames) {
                var frameStack = frame.getItem();
                if (frameStack.isEmpty() || frameStack.getItem() != ModItems.CLOCK_HAND)
                    continue;

                if (this.goalTime > 0) {
                    var current = this.level.getDayTime();
                    var toAdd = Math.min(75, this.goalTime - current);
                    if (toAdd <= 0 || !this.canUseRightNow((int) toAdd * 20)) {
                        this.goalTime = 0;
                        this.sendToClients();
                        return;
                    }
                    ((ServerLevelData) this.level.getLevelData()).setDayTime(current + toAdd);

                    var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                    IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, (int) toAdd * 20);

                    if (this.level instanceof ServerLevel) {
                        var list = this.level.getServer().getPlayerList();
                        list.broadcastAll(new ClientboundSetTimePacket(
                                this.level.getGameTime(), this.level.getDayTime(),
                                this.level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                    }
                    return;
                }

                if (this.level.getGameTime() % 20 != 0)
                    return;

                var items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.worldPosition).inflate(1), Entity::isAlive);
                for (var item : items) {
                    if (item.hasPickUpDelay())
                        continue;
                    var stack = item.getItem();
                    if (stack.isEmpty() || stack.getItem() != Items.CLOCK)
                        continue;

                    var dayGoal = Mth.floor(frame.getRotation() / 8F * 24000F) + 18000;
                    var current = this.level.getDayTime();
                    var toMove = (24000 - current % 24000 + dayGoal) % 24000;
                    this.goalTime = current + toMove;
                    this.sendToClients();

                    if (stack.getCount() <= 1) {
                        item.kill();
                    } else {
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
        } else if (this.goalTime > 0 && this.level.random.nextFloat() >= 0.25F) {
            var angle = Math.toRadians(this.level.getDayTime() * 5F % 360);
            var x = this.worldPosition.getX() + 0.5 + Math.sin(angle) * 3F;
            var z = this.worldPosition.getZ() + 0.5 + Math.cos(angle) * 3F;
            var color = this.goalTime % 24000 > 12000 ? 0xe2e2e2 : 0xffe926;
            NaturesAuraAPI.instance().spawnMagicParticle(
                    x, this.worldPosition.getY() + 0.1F, z,
                    0F, 0.12F, 0F,
                    color, 1F + this.level.random.nextFloat() * 2F,
                    this.level.random.nextInt(100) + 100, 0, false, true);
            NaturesAuraAPI.instance().spawnMagicParticle(
                    x, this.worldPosition.getY() + 0.1F, z,
                    0F, 0F, 0F,
                    IAuraType.forLevel(this.level).getColor(), 1F + this.level.random.nextFloat(),
                    150, 0, false, true);
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type != SaveType.BLOCK)
            compound.putLong("goal", this.goalTime);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type != SaveType.BLOCK)
            this.goalTime = compound.getLong("goal");
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }
}
