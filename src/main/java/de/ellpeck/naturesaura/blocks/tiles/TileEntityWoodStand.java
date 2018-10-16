package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    private Map<TileEntityWoodStand, ItemStack> involvedStands;
    private TreeRitualRecipe recipe;
    private int timer;

    public void setRitual(BlockPos pos, TreeRitualRecipe recipe, Map<TileEntityWoodStand, ItemStack> involvedStands) {
        this.ritualPos = pos;
        this.recipe = recipe;
        this.involvedStands = involvedStands;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.ritualPos != null && this.involvedStands != null && this.recipe != null) {
                if (this.isRitualOkay(this.world)) {
                    this.timer++;

                    if (this.timer % 3 == 0) {
                        for (TileEntityWoodStand stand : this.involvedStands.keySet()) {
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                    (float) stand.pos.getX() + 0.5F, (float) stand.pos.getY() + 1.25F, (float) stand.pos.getZ() + 0.5F,
                                    this.ritualPos.getX() + 0.5F, this.ritualPos.getY() + 2.5F, this.ritualPos.getZ() + 0.5F,
                                    this.world.rand.nextFloat() * 0.02F + 0.02F, 0xFF00FF, this.world.rand.nextFloat() * 1F + 1F
                            ));
                        }
                    }
                    if (this.timer % 5 == 0) {
                        PacketHandler.sendToAllAround(this.world, this.ritualPos, 32,
                                new PacketParticles(this.ritualPos.getX(), this.ritualPos.getY(), this.ritualPos.getZ(), 0));
                    }

                    if (this.timer >= this.recipe.time) {
                        this.recurseTreeDestruction(this.ritualPos, this.ritualPos);
                        for (BlockPos offset : GOLD_POWDER_POSITIONS) {
                            this.world.setBlockToAir(this.ritualPos.add(offset));
                        }

                        EntityItem item = new EntityItem(this.world,
                                this.ritualPos.getX() + 0.5, this.ritualPos.getY() + 4.5, this.ritualPos.getZ() + 0.5,
                                this.recipe.result.copy());
                        this.world.spawnEntity(item);

                        PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 3));

                        this.ritualPos = null;
                        this.involvedStands = null;
                        this.recipe = null;
                        this.timer = 0;
                    } else if (this.timer >= this.recipe.time / 2 && !this.involvedStands.isEmpty()) {
                        for (TileEntityWoodStand stand : this.involvedStands.keySet()) {
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(stand.pos.getX(), stand.pos.getY(), stand.pos.getZ(), 1));
                            stand.stack = ItemStack.EMPTY;
                            stand.sendToClients();
                        }
                        this.involvedStands.clear();
                    }

                } else {
                    this.ritualPos = null;
                    this.involvedStands = null;
                    this.recipe = null;
                    this.timer = 0;
                }
            }
        }
    }

    private void recurseTreeDestruction(BlockPos pos, BlockPos start) {
        if (pos.distanceSq(start) >= 15 * 15) {
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

    private boolean isRitualOkay(World world) {
        for (Map.Entry<TileEntityWoodStand, ItemStack> entry : this.involvedStands.entrySet()) {
            TileEntityWoodStand stand = entry.getKey();
            if (stand.isInvalid() || !stand.stack.isItemEqual(entry.getValue())) {
                return false;
            }
        }
        return Helper.checkMultiblock(world, this.ritualPos, TileEntityWoodStand.GOLD_POWDER_POSITIONS, ModBlocks.GOLD_POWDER.getDefaultState(), true);
    }

    @Override
    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeNBT(compound, syncing);
        compound.setTag("item", this.stack.writeToNBT(new NBTTagCompound()));
        //TODO Save info about the current ritual somehow
    }

    @Override
    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readNBT(compound, syncing);
        this.stack = new ItemStack(compound.getCompoundTag("item"));
    }
}
