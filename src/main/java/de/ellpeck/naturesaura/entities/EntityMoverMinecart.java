package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityMoverMinecart extends EntityMinecart {

    private final List<BlockPos> spotOffsets = new ArrayList<>();
    private BlockPos lastPosition = BlockPos.ORIGIN;
    public boolean isActive;

    public EntityMoverMinecart(World worldIn) {
        super(worldIn);
    }

    public EntityMoverMinecart(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void moveMinecartOnRail(BlockPos railPos) {
        super.moveMinecartOnRail(railPos);
        if (!this.isActive)
            return;
        BlockPos pos = this.getPosition();

        if (!this.spotOffsets.isEmpty() && this.world.getTotalWorldTime() % 10 == 0)
            PacketHandler.sendToAllAround(this.world, pos, 32, new PacketParticles(
                    (float) this.posX, (float) this.posY, (float) this.posZ, 22,
                    MathHelper.floor(this.motionX * 100F), MathHelper.floor(this.motionY * 100F), MathHelper.floor(this.motionZ * 100F)));

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
                this.lastPosition = BlockPos.ORIGIN;
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
        this.setDead();
        if (this.world.getGameRules().getBoolean("doEntityDrops"))
            this.entityDropItem(new ItemStack(ModItems.MOVER_MINECART), 0);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("active", this.isActive);
        compound.setLong("last_pos", this.lastPosition.toLong());

        NBTTagList list = new NBTTagList();
        for (BlockPos offset : this.spotOffsets)
            list.appendTag(new NBTTagLong(offset.toLong()));
        compound.setTag("offsets", list);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.isActive = compound.getBoolean("active");
        this.lastPosition = BlockPos.fromLong(compound.getLong("last_pos"));

        this.spotOffsets.clear();
        NBTTagList list = compound.getTagList("offsets", Constants.NBT.TAG_LONG);
        for (NBTBase base : list)
            this.spotOffsets.add(BlockPos.fromLong(((NBTTagLong) base).getLong()));
    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        Entity entity = super.changeDimension(dimensionIn, teleporter);
        if (entity instanceof EntityMoverMinecart) {
            BlockPos pos = entity.getPosition();
            this.moveAura(this.world, this.lastPosition, entity.world, pos);
            ((EntityMoverMinecart) entity).lastPosition = pos;
        }
        return entity;
    }

    @Override
    public IBlockState getDisplayTile() {
        return Blocks.STONE.getDefaultState();
    }

    @Override
    public Type getType() {
        return Type.RIDEABLE;
    }

    @Override
    public ItemStack getCartItem() {
        return new ItemStack(ModItems.MOVER_MINECART);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ModItems.MOVER_MINECART);
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void applyDrag() {
        this.motionX *= 0.99F;
        this.motionY *= 0.0D;
        this.motionZ *= 0.99F;
    }
}
