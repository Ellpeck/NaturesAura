package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.server.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.level.BlockEvent;

import java.util.List;

public class BlockEntityFieldCreator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockPos connectionOffset;
    public boolean isMain;
    public boolean isCharged;
    private int chargeTimer;

    public BlockEntityFieldCreator() {
        super(ModTileEntities.FIELD_CREATOR);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide || this.level.getGameTime() % 10 != 0)
            return;

        BlockPos connectedPos = this.getConnectedPos();
        if (connectedPos == null || !this.level.isBlockLoaded(connectedPos))
            return;

        BlockEntity other = this.level.getBlockEntity(connectedPos);
        if (!this.isCloseEnough(connectedPos)
                || !(other instanceof BlockEntityFieldCreator)
                || !this.worldPosition.equals(((BlockEntityFieldCreator) other).getConnectedPos())) {
            this.connectionOffset = null;
            this.chargeTimer = 0;
            this.isCharged = false;
            this.isMain = false;
            this.sendToClients();
            return;
        }

        if (!this.isMain)
            return;

        BlockEntityFieldCreator creator = (BlockEntityFieldCreator) other;
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

        BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 32, this.worldPosition);
        IAuraChunk chunk = IAuraChunk.getAuraChunk(this.level, spot);

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
            if (this.level.getGameTime() % 40 == 0)
                chunk.drainAura(spot, 20);

            ItemStack tool = this.getToolUsed(creator);
            Vector3d dist = new Vector3d(
                    this.worldPosition.getX() - connectedPos.getX(),
                    this.worldPosition.getY() - connectedPos.getY(),
                    this.worldPosition.getZ() - connectedPos.getZ()
            );
            double length = dist.length();
            Vector3d normal = new Vector3d(dist.x / length, dist.y / length, dist.z / length);
            for (float i = Mth.floor(length); i > 0; i -= 0.5F) {
                Vector3d scaled = normal.scale(i);
                BlockPos pos = connectedPos.add(
                        Mth.floor(scaled.x + 0.5F),
                        Mth.floor(scaled.y + 0.5F),
                        Mth.floor(scaled.z + 0.5F));

                if (pos.equals(this.worldPosition) || pos.equals(connectedPos))
                    continue;

                BlockState state = this.level.getBlockState(pos);
                Block block = state.getBlock();
                if (!block.isAir(state, this.level, pos) && state.getBlockHardness(this.level, pos) >= 0F) {
                    FakePlayer fake = FakePlayerFactory.getMinecraft((ServerLevel) this.level);
                    if (!MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(this.level, pos, state, fake))) {
                        List<ItemStack> drops = state.getDrops(new LootContext.Builder((ServerLevel) this.level)
                                .withParameter(LootParameters.THIS_ENTITY, fake)
                                .withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos))
                                .withParameter(LootParameters.BLOCK_STATE, state)
                                .withParameter(LootParameters.TOOL, tool.isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : tool)
                                .withNullableParameter(LootParameters.BLOCK_ENTITY, this.level.getBlockEntity(pos)));
                        this.level.destroyBlock(pos, false);
                        for (ItemStack stack : drops)
                            Block.spawnAsEntity(this.level, pos, stack);
                        chunk.drainAura(spot, !tool.isEmpty() ? 300 : 100);
                        this.sendParticles();
                    }
                }
            }
        }
    }

    private ItemStack getToolUsed(BlockEntityFieldCreator other) {
        ItemStack myTool = this.getMyTool();
        ItemStack otherTool = other.getMyTool();
        if (!myTool.isEmpty()) {
            // if both have tools, choose randomly
            if (!otherTool.isEmpty())
                return this.level.rand.nextBoolean() ? myTool : otherTool;
            return myTool;
        }
        return otherTool;
    }

    private ItemStack getMyTool() {
        List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
        for (ItemFrameEntity frame : frames) {
            ItemStack stack = frame.getDisplayedItem();
            if (!stack.isEmpty())
                return stack;
        }
        return ItemStack.EMPTY;
    }

    private void sendParticles() {
        for (int j = 0; j < 2; j++) {
            BlockPos p = j == 0 ? this.worldPosition : this.getConnectedPos();
            PacketHandler.sendToAllAround(this.level, p, 32, new PacketParticleStream(
                    p.getX() + (float) this.level.rand.nextGaussian() * 3F,
                    p.getY() + 1 + this.level.rand.nextFloat() * 3F,
                    p.getZ() + (float) this.level.rand.nextGaussian() * 3F,
                    p.getX() + 0.5F,
                    p.getY() + 0.5F,
                    p.getZ() + 0.5F,
                    this.level.rand.nextFloat() * 0.07F + 0.07F, IAuraType.forLevel(this.level).getColor(), this.level.rand.nextFloat() + 0.5F
            ));
        }
    }

    public boolean isCloseEnough(BlockPos pos) {
        int range = ModConfig.instance.fieldCreatorRange.get() + 1;
        return this.worldPosition.distanceSq(pos) <= range * range;
    }

    public BlockPos getConnectedPos() {
        if (this.connectionOffset == null)
            return null;
        return this.worldPosition.add(this.connectionOffset);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
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
    public void readNBT(CompoundTag compound, SaveType type) {
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
