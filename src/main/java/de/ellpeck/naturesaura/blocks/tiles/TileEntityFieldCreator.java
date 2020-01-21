package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

public class TileEntityFieldCreator extends TileEntityImpl implements ITickableTileEntity {

    public BlockPos connectionOffset;
    public boolean isMain;
    public boolean isCharged;
    private int chargeTimer;

    public TileEntityFieldCreator(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (this.world.isRemote || this.world.getGameTime() % 10 != 0)
            return;

        BlockPos connectedPos = this.getConnectedPos();
        if (connectedPos == null || !this.world.isBlockLoaded(connectedPos))
            return;

        TileEntity other = this.world.getTileEntity(connectedPos);
        if (!this.isCloseEnough(connectedPos)
                || !(other instanceof TileEntityFieldCreator)
                || !this.pos.equals(((TileEntityFieldCreator) other).getConnectedPos())) {
            this.connectionOffset = null;
            this.chargeTimer = 0;
            this.isCharged = false;
            this.isMain = false;
            this.sendToClients();
            return;
        }

        if (!this.isMain)
            return;

        TileEntityFieldCreator creator = (TileEntityFieldCreator) other;
        if (this.redstonePower <= 0 && creator.redstonePower <= 0) {
            this.chargeTimer = 0;
            if (this.isCharged) {
                this.isCharged = false;
                this.sendToClients();
                creator.isCharged = false;
                creator.sendToClients();
            }
            return;
        }

        BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 32, this.pos);
        IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, spot);

        if (!this.isCharged) {
            this.chargeTimer += 10;
            if (this.chargeTimer >= 150) {
                this.chargeTimer = 0;

                this.isCharged = true;
                this.sendToClients();
                creator.isCharged = true;
                creator.sendToClients();
            }

            chunk.drainAura(spot, 300);
            this.sendParticles();
        } else {
            if (this.world.getGameTime() % 40 == 0)
                chunk.drainAura(spot, 100);

            boolean shears = this.shears() || creator.shears();
            Vec3d dist = new Vec3d(
                    this.pos.getX() - connectedPos.getX(),
                    this.pos.getY() - connectedPos.getY(),
                    this.pos.getZ() - connectedPos.getZ()
            );
            double length = dist.length();
            Vec3d normal = new Vec3d(dist.x / length, dist.y / length, dist.z / length);
            for (int i = MathHelper.floor(length); i > 0; i--) {
                Vec3d scaled = normal.scale(i);
                BlockPos pos = connectedPos.add(
                        MathHelper.floor(scaled.x + 0.5F),
                        MathHelper.floor(scaled.y + 0.5F),
                        MathHelper.floor(scaled.z + 0.5F));

                if (pos.equals(this.pos) || pos.equals(connectedPos))
                    continue;

                BlockState state = this.world.getBlockState(pos);
                Block block = state.getBlock();
                if (!block.isAir(state, this.world, pos) && state.getBlockHardness(this.world, pos) >= 0F) {
                    FakePlayer fake = FakePlayerFactory.getMinecraft((ServerWorld) this.world);
                    if (!MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(this.world, pos, state, fake))) {
                        boolean shearBlock = shears && block instanceof IShearable;
                        List<ItemStack> drops;
                        if (shearBlock && ((IShearable) block).isShearable(ItemStack.EMPTY, this.world, pos))
                            drops = ((IShearable) block).onSheared(ItemStack.EMPTY, this.world, pos, 0);
                        else {
                            drops = state.getDrops(new LootContext.Builder((ServerWorld) this.world)
                                    .withParameter(LootParameters.POSITION, pos)
                                    .withParameter(LootParameters.BLOCK_STATE, state));
                        }
                        float chance = ForgeEventFactory.fireBlockHarvesting((NonNullList<ItemStack>) drops, this.world, pos, state, 0, 1, false, fake);
                        if (chance > 0 && this.world.rand.nextFloat() <= chance) {
                            this.world.destroyBlock(pos, false);
                            for (ItemStack stack : drops)
                                Block.spawnAsEntity(this.world, pos, stack);

                            chunk.drainAura(spot, shearBlock ? 1000 : 300);
                            this.sendParticles();
                        }
                    }
                }
            }
        }
    }

    public boolean shears() {
        List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.world, this.pos);
        for (ItemFrameEntity frame : frames) {
            ItemStack stack = frame.getDisplayedItem();
            if (!stack.isEmpty() && stack.getItem() instanceof ShearsItem)
                return true;
        }
        return false;
    }

    private void sendParticles() {
        for (int j = 0; j < 2; j++) {
            BlockPos p = j == 0 ? this.pos : this.getConnectedPos();
            // TODO particles
           /* PacketHandler.sendToAllAround(this.world, p, 32, new PacketParticleStream(
                    p.getX() + (float) this.world.rand.nextGaussian() * 3F,
                    p.getY() + 1 + this.world.rand.nextFloat() * 3F,
                    p.getZ() + (float) this.world.rand.nextGaussian() * 3F,
                    p.getX() + 0.5F,
                    p.getY() + 0.5F,
                    p.getZ() + 0.5F,
                    this.world.rand.nextFloat() * 0.07F + 0.07F, IAuraType.forWorld(this.world).getColor(), this.world.rand.nextFloat() + 0.5F
            ));*/
        }
    }

    public boolean isCloseEnough(BlockPos pos) {
        int range = ModConfig.general.fieldCreatorRange + 1;
        return this.pos.distanceSq(pos) <= range * range;
    }

    public BlockPos getConnectedPos() {
        if (this.connectionOffset == null)
            return null;
        return this.pos.add(this.connectionOffset);
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (this.connectionOffset != null)
                compound.putLong("connection", this.connectionOffset.toLong());
            compound.putBoolean("main", this.isMain);
            compound.putBoolean("charged", this.isCharged);

            if (type == SaveType.TILE)
                compound.putInt("timer", this.chargeTimer);
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (compound.contains("connection"))
                this.connectionOffset = BlockPos.fromLong(compound.getLong("connection"));
            else
                this.connectionOffset = null;
            this.isMain = compound.getBoolean("main");
            this.isCharged = compound.getBoolean("charged");

            if (type == SaveType.TILE)
                this.chargeTimer = compound.getInt("timer");
        }
    }
}
