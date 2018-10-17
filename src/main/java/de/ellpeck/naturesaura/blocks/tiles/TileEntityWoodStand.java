package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class TileEntityWoodStand extends TileEntityImpl implements ITickable {

    public static final BlockPos[] GOLD_POWDER_POSITIONS = new BlockPos[]{
            new BlockPos(-2, 0, 0),
            new BlockPos(2, 0, 0),
            new BlockPos(0, 0, -2),
            new BlockPos(0, 0, 2),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(2, 0, -1),
            new BlockPos(2, 0, 1),
            new BlockPos(-2, 0, -1),
            new BlockPos(-2, 0, 1),
            new BlockPos(1, 0, 2),
            new BlockPos(-1, 0, 2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, -2)
    };
    public ItemStack stack = ItemStack.EMPTY;

    private BlockPos ritualPos;
    private Map<BlockPos, ItemStack> involvedStands;
    private ItemStack output;
    private int totalTime;
    private int timer;

    public void setRitual(BlockPos pos, ItemStack output, int totalTime, Map<BlockPos, ItemStack> involvedStands) {
        this.ritualPos = pos;
        this.output = output;
        this.totalTime = totalTime;
        this.involvedStands = involvedStands;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.ritualPos != null && this.involvedStands != null && this.output != null && this.totalTime > 0) {
                if (this.isRitualOkay()) {
                    this.timer++;

                    if (this.timer % 5 == 0 && this.timer < this.totalTime / 2) {
                        for (BlockPos pos : this.involvedStands.keySet()) {
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                    (float) pos.getX() + 0.4F + this.world.rand.nextFloat() * 0.2F,
                                    (float) pos.getY() + 1.05F + this.world.rand.nextFloat() * 0.35F,
                                    (float) pos.getZ() + 0.4F + this.world.rand.nextFloat() * 0.2F,
                                    this.ritualPos.getX() + 0.5F, this.ritualPos.getY() + this.world.rand.nextFloat() * 2F + 1F, this.ritualPos.getZ() + 0.5F,
                                    this.world.rand.nextFloat() * 0.02F + 0.02F, 0x89cc37, this.world.rand.nextFloat() * 1F + 1F
                            ));
                        }
                    }
                    if (this.timer % 5 == 0) {
                        PacketHandler.sendToAllAround(this.world, this.ritualPos, 32,
                                new PacketParticles(this.ritualPos.getX(), this.ritualPos.getY(), this.ritualPos.getZ(), 0));
                    }

                    if (this.timer >= this.totalTime) {
                        this.recurseTreeDestruction(this.ritualPos, this.ritualPos);
                        for (BlockPos offset : GOLD_POWDER_POSITIONS) {
                            this.world.setBlockToAir(this.ritualPos.add(offset));
                        }

                        EntityItem item = new EntityItem(this.world,
                                this.ritualPos.getX() + 0.5, this.ritualPos.getY() + 4.5, this.ritualPos.getZ() + 0.5,
                                this.output.copy());
                        this.world.spawnEntity(item);

                        PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 3));

                        this.ritualPos = null;
                        this.involvedStands = null;
                        this.output = null;
                        this.totalTime = 0;
                        this.timer = 0;
                    } else if (this.timer == this.totalTime / 2) {
                        for (BlockPos pos : this.involvedStands.keySet()) {
                            TileEntityWoodStand stand = (TileEntityWoodStand) this.world.getTileEntity(pos);
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(stand.pos.getX(), stand.pos.getY(), stand.pos.getZ(), 1));
                            stand.stack = ItemStack.EMPTY;
                            stand.sendToClients();
                        }
                    }

                } else {
                    this.ritualPos = null;
                    this.involvedStands = null;
                    this.output = null;
                    this.totalTime = 0;
                    this.timer = 0;
                }
            }
        }
    }

    private void recurseTreeDestruction(BlockPos pos, BlockPos start) {
        if (Math.abs(pos.getX() - start.getX()) >= 6
                || Math.abs(pos.getZ() - start.getZ()) >= 6
                || Math.abs(pos.getY() - start.getY()) >= 16) {
            return;
        }

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos offset = pos.offset(facing);
            IBlockState state = this.world.getBlockState(offset);
            if (state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLeaves) {
                this.world.setBlockToAir(offset);
                PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), 2));

                this.recurseTreeDestruction(offset, start);
            }
        }
    }

    private boolean isRitualOkay() {
        for (int i = 0; i < 3; i++) {
            IBlockState state = this.world.getBlockState(this.ritualPos.up(i));
            if (!(state.getBlock() instanceof BlockLog)) {
                return false;
            }
        }
        for (Map.Entry<BlockPos, ItemStack> entry : this.involvedStands.entrySet()) {
            TileEntity tile = this.world.getTileEntity(entry.getKey());
            if (!(tile instanceof TileEntityWoodStand) || (this.timer < this.totalTime / 2 && !((TileEntityWoodStand) tile).stack.isItemEqual(entry.getValue()))) {
                return false;
            }
        }
        return Helper.checkMultiblock(this.world, this.ritualPos, TileEntityWoodStand.GOLD_POWDER_POSITIONS, ModBlocks.GOLD_POWDER.getDefaultState(), true);
    }

    @Override
    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeNBT(compound, syncing);
        compound.setTag("item", this.stack.writeToNBT(new NBTTagCompound()));

        if (!syncing) {
            if (this.ritualPos != null && this.involvedStands != null && this.output != null && this.totalTime > 0) {
                compound.setLong("ritual_pos", this.ritualPos.toLong());
                compound.setInteger("timer", this.timer);
                compound.setInteger("total_time", this.totalTime);
                compound.setTag("output", this.output.writeToNBT(new NBTTagCompound()));

                NBTTagList list = new NBTTagList();
                for (Map.Entry<BlockPos, ItemStack> entry : this.involvedStands.entrySet()) {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setLong("pos", entry.getKey().toLong());
                    tag.setTag("item", entry.getValue().writeToNBT(new NBTTagCompound()));
                    list.appendTag(tag);
                }
                compound.setTag("stands", list);
            }
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readNBT(compound, syncing);
        this.stack = new ItemStack(compound.getCompoundTag("item"));

        if (!syncing) {
            if (compound.hasKey("ritual_pos") && compound.hasKey("stands") && compound.hasKey("output") && compound.hasKey("total_time")) {
                this.ritualPos = BlockPos.fromLong(compound.getLong("ritual_pos"));
                this.timer = compound.getInteger("timer");
                this.totalTime = compound.getInteger("total_time");
                this.output = new ItemStack(compound.getCompoundTag("output"));

                this.involvedStands = new HashMap<>();
                NBTTagList list = compound.getTagList("stands", 10);
                for (NBTBase base : list) {
                    NBTTagCompound tag = (NBTTagCompound) base;
                    this.involvedStands.put(
                            BlockPos.fromLong(tag.getLong("pos")),
                            new ItemStack(tag.getCompoundTag("item"))
                    );
                }
            }
        }
    }
}
