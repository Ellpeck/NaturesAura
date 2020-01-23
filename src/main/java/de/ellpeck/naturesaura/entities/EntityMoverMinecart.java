package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityMoverMinecart extends AbstractMinecartEntity {

    private final List<BlockPos> spotOffsets = new ArrayList<>();
    private BlockPos lastPosition = BlockPos.ZERO;
    public boolean isActive;

    public EntityMoverMinecart(EntityType<?> type, World world) {
        super(type, world);
    }

    public EntityMoverMinecart(EntityType<?> type, World world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    @Override
    public void moveMinecartOnRail(BlockPos railPos) {
        super.moveMinecartOnRail(railPos);
        if (!this.isActive)
            return;
        BlockPos pos = this.getPosition();

        if (!this.spotOffsets.isEmpty() && this.world.getGameTime() % 10 == 0)
            PacketHandler.sendToAllAround(this.world, pos, 32, new PacketParticles(
                    (float) this.posX, (float) this.posY, (float) this.posZ, 22,
                    MathHelper.floor(this.getMotion().getX() * 100F), MathHelper.floor(this.getMotion().getY() * 100F), MathHelper.floor(this.getMotion().getZ() * 100F)));

        if (pos.distanceSq(this.lastPosition) < 8 * 8)
            return;

        this.moveAura(this.world, this.lastPosition, this.world, pos);
        this.lastPosition = pos;
    }

    private void moveAura(World oldWorld, BlockPos oldPos, World newWorld, BlockPos newPos) {
        for (BlockPos offset : this.spotOffsets) {
            BlockPos spot = oldPos.add(offset);
            IAuraChunk chunk = IAuraChunk.getAuraChunk(oldWorld, spot);
            int amount = chunk.getDrainSpot(spot);
            if (amount <= 0)
                continue;
            int toMove = Math.min(amount, 300000);
            int drained = chunk.drainAura(spot, toMove, false, false);
            if (drained <= 0)
                continue;
            int toLose = MathHelper.ceil(drained / 250F);
            BlockPos newSpot = newPos.add(offset);
            IAuraChunk.getAuraChunk(newWorld, newSpot).storeAura(newSpot, drained - toLose, false, false);
        }
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (this.isActive != receivingPower) {
            this.isActive = receivingPower;

            if (!this.isActive) {
                this.spotOffsets.clear();
                this.lastPosition = BlockPos.ZERO;
                return;
            }

            BlockPos pos = this.getPosition();
            IAuraChunk.getSpotsInArea(this.world, pos, 25, (spot, amount) -> {
                if (amount > 0)
                    this.spotOffsets.add(spot.subtract(pos));
            });
            this.lastPosition = pos;
        }
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.remove();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
            this.entityDropItem(new ItemStack(ModItems.MOVER_CART), 0);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.putBoolean("active", this.isActive);
        compound.putLong("last_pos", this.lastPosition.toLong());

        ListNBT list = new ListNBT();
        for (BlockPos offset : this.spotOffsets)
            list.add(new LongNBT(offset.toLong()));
        compound.put("offsets", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        this.isActive = compound.getBoolean("active");
        this.lastPosition = BlockPos.fromLong(compound.getLong("last_pos"));

        this.spotOffsets.clear();
        ListNBT list = compound.getList("offsets", Constants.NBT.TAG_LONG);
        for (INBT base : list)
            this.spotOffsets.add(BlockPos.fromLong(((LongNBT) base).getLong()));
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionType destination) {
        Entity entity = super.changeDimension(destination);
        if (entity instanceof EntityMoverMinecart) {
            BlockPos pos = entity.getPosition();
            this.moveAura(this.world, this.lastPosition, entity.world, pos);
            ((EntityMoverMinecart) entity).lastPosition = pos;
        }
        return entity;
    }

    @Override
    public BlockState getDisplayTile() {
        return Blocks.STONE.getDefaultState();
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public ItemStack getCartItem() {
        return new ItemStack(ModItems.MOVER_CART);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ModItems.MOVER_CART);
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void applyDrag() {
        Vec3d motion = this.getMotion();
        this.setMotion(motion.x * 0.99F, 0, motion.z * 0.99F);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
