package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

public class BlockEntityFieldCreator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockPos connectionOffset;
    public boolean isMain;
    public boolean isCharged;
    private int chargeTimer;

    public BlockEntityFieldCreator(BlockPos pos, BlockState state) {
        super(ModTileEntities.FIELD_CREATOR, pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide || this.level.getGameTime() % 10 != 0)
            return;

        BlockPos connectedPos = this.getConnectedPos();
        if (connectedPos == null || !this.level.isLoaded(connectedPos))
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
            Vec3 dist = new Vec3(
                    this.worldPosition.getX() - connectedPos.getX(),
                    this.worldPosition.getY() - connectedPos.getY(),
                    this.worldPosition.getZ() - connectedPos.getZ()
            );
            double length = dist.length();
            Vec3 normal = new Vec3(dist.x / length, dist.y / length, dist.z / length);
            for (float i = Mth.floor(length); i > 0; i -= 0.5F) {
                Vec3 scaled = normal.scale(i);
                BlockPos pos = connectedPos.offset(
                        Mth.floor(scaled.x + 0.5F),
                        Mth.floor(scaled.y + 0.5F),
                        Mth.floor(scaled.z + 0.5F));

                if (pos.equals(this.worldPosition) || pos.equals(connectedPos))
                    continue;

                BlockState state = this.level.getBlockState(pos);
                Block block = state.getBlock();
                if (!state.isAir() && state.getDestroySpeed(this.level, pos) >= 0F) {
                    FakePlayer fake = FakePlayerFactory.getMinecraft((ServerLevel) this.level);
                    if (!MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(this.level, pos, state, fake))) {
                        List<ItemStack> drops = state.getDrops(new LootContext.Builder((ServerLevel) this.level)
                                .withParameter(LootContextParams.THIS_ENTITY, fake)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                .withParameter(LootContextParams.BLOCK_STATE, state)
                                .withParameter(LootContextParams.TOOL, tool.isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : tool)
                                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, this.level.getBlockEntity(pos)));
                        this.level.destroyBlock(pos, false);
                        for (ItemStack stack : drops)
                            Block.popResource(this.level, pos, stack);
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
                return this.level.random.nextBoolean() ? myTool : otherTool;
            return myTool;
        }
        return otherTool;
    }

    private ItemStack getMyTool() {
        List<ItemFrame> frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
        for (ItemFrame frame : frames) {
            ItemStack stack = frame.getItem();
            if (!stack.isEmpty())
                return stack;
        }
        return ItemStack.EMPTY;
    }

    private void sendParticles() {
        for (int j = 0; j < 2; j++) {
            BlockPos p = j == 0 ? this.worldPosition : this.getConnectedPos();
            PacketHandler.sendToAllAround(this.level, p, 32, new PacketParticleStream(
                    p.getX() + (float) this.level.random.nextGaussian() * 3F,
                    p.getY() + 1 + this.level.random.nextFloat() * 3F,
                    p.getZ() + (float) this.level.random.nextGaussian() * 3F,
                    p.getX() + 0.5F,
                    p.getY() + 0.5F,
                    p.getZ() + 0.5F,
                    this.level.random.nextFloat() * 0.07F + 0.07F, IAuraType.forLevel(this.level).getColor(), this.level.random.nextFloat() + 0.5F
            ));
        }
    }

    public boolean isCloseEnough(BlockPos pos) {
        int range = ModConfig.instance.fieldCreatorRange.get() + 1;
        return this.worldPosition.distSqr(pos) <= range * range;
    }

    public BlockPos getConnectedPos() {
        if (this.connectionOffset == null)
            return null;
        return this.worldPosition.offset(this.connectionOffset);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (this.connectionOffset != null)
                compound.putLong("connection", this.connectionOffset.asLong());
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
                this.connectionOffset = BlockPos.of(compound.getLong("connection"));
            else
                this.connectionOffset = null;
            this.isMain = compound.getBoolean("main");
            this.isCharged = compound.getBoolean("charged");

            if (type == SaveType.TILE)
                this.chargeTimer = compound.getInt("timer");
        }
    }
}
