package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileEntityWoodStand extends TileEntityImpl implements ITickable {

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private TreeRitualRecipe recipe;
    private BlockPos ritualPos;
    private int timer;

    public void setRitual(BlockPos pos, TreeRitualRecipe recipe) {
        this.ritualPos = pos;
        this.recipe = recipe;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.ritualPos != null && this.recipe != null) {
                if (this.world.getTotalWorldTime() % 5 == 0) {
                    if (this.isRitualOkay()) {
                        boolean wasOverHalf = this.timer >= this.recipe.time / 2;
                        this.timer += 5;
                        boolean isOverHalf = this.timer >= this.recipe.time / 2;

                        if (!isOverHalf)
                            Multiblocks.TREE_RITUAL.forEach(this.world, this.ritualPos, Rotation.NONE, 'W', pos -> {
                                TileEntity tile = this.world.getTileEntity(pos);
                                if (tile instanceof TileEntityWoodStand && !((TileEntityWoodStand) tile).items.getStackInSlot(0).isEmpty()) {
                                    PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                            (float) pos.getX() + 0.2F + this.world.rand.nextFloat() * 0.6F,
                                            (float) pos.getY() + 0.85F,
                                            (float) pos.getZ() + 0.2F + this.world.rand.nextFloat() * 0.6F,
                                            this.ritualPos.getX() + 0.5F, this.ritualPos.getY() + this.world.rand.nextFloat() * 3F + 2F, this.ritualPos.getZ() + 0.5F,
                                            this.world.rand.nextFloat() * 0.02F + 0.02F, 0x89cc37, this.world.rand.nextFloat() * 1F + 1F
                                    ));
                                }
                            });

                        PacketHandler.sendToAllAround(this.world, this.ritualPos, 32,
                                new PacketParticles(this.ritualPos.getX(), this.ritualPos.getY(), this.ritualPos.getZ(), 0));

                        if (this.timer >= this.recipe.time) {
                            this.recurseTreeDestruction(this.ritualPos, this.ritualPos);
                            Multiblocks.TREE_RITUAL.forEach(this.world, this.ritualPos, Rotation.NONE, 'G',
                                    pos -> this.world.setBlockToAir(pos));

                            EntityItem item = new EntityItem(this.world,
                                    this.ritualPos.getX() + 0.5, this.ritualPos.getY() + 4.5, this.ritualPos.getZ() + 0.5,
                                    this.recipe.result.copy());
                            this.world.spawnEntity(item);

                            PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                    new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 3));
                            this.world.playSound(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                                    SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.65F, 1F);

                            this.ritualPos = null;
                            this.recipe = null;
                            this.timer = 0;
                        } else if (isOverHalf && !wasOverHalf) {
                            Multiblocks.TREE_RITUAL.forEach(this.world, this.ritualPos, Rotation.NONE, 'W', pos -> {
                                TileEntity tile = this.world.getTileEntity(pos);
                                if (tile instanceof TileEntityWoodStand) {
                                    TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                                    if (!stand.items.getStackInSlot(0).isEmpty()) {
                                        PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                                new PacketParticles(stand.pos.getX(), stand.pos.getY(), stand.pos.getZ(), 1));
                                        this.world.playSound(null, stand.pos.getX() + 0.5, stand.pos.getY() + 0.5, stand.pos.getZ() + 0.5,
                                                SoundEvents.BLOCK_WOOD_STEP, SoundCategory.BLOCKS, 0.5F, 1F);

                                        stand.items.setStackInSlot(0, ItemStack.EMPTY);
                                        stand.sendToClients();
                                    }
                                }
                            });
                        }
                    } else {
                        this.ritualPos = null;
                        this.recipe = null;
                        this.timer = 0;
                    }
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
        if (!Multiblocks.TREE_RITUAL.forEachMatcher(this.world, this.ritualPos, Rotation.NONE, (char) 0, (start, actionPos, x, y, z, ch, matcher) ->
                matcher.displayState.getBlock() == ModBlocks.WOOD_STAND || Multiblocks.TREE_RITUAL.test(this.world, start, x, y, z, Rotation.NONE))) {
            return false;
        }
        if (this.timer < this.recipe.time / 2) {
            List<ItemStack> required = new ArrayList<>(Arrays.asList(this.recipe.items));
            MutableBoolean tooMuch = new MutableBoolean();
            Multiblocks.TREE_RITUAL.forEach(this.world, this.ritualPos, Rotation.NONE, 'W', pos -> {
                TileEntity tile = this.world.getTileEntity(pos);
                if (tile instanceof TileEntityWoodStand) {
                    ItemStack stack = ((TileEntityWoodStand) tile).items.getStackInSlot(0);
                    if (!stack.isEmpty()) {
                        int index = Helper.getItemIndex(required, stack);
                        if (index >= 0) {
                            required.remove(index);
                        } else {
                            tooMuch.setTrue();
                        }
                    }
                }
            });
            return tooMuch.isFalse() && required.isEmpty();
        } else
            return true;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeNBT(compound, syncing);
        compound.setTag("items", this.items.serializeNBT());

        if (!syncing) {
            if (this.ritualPos != null && this.recipe != null) {
                compound.setLong("ritual_pos", this.ritualPos.toLong());
                compound.setInteger("timer", this.timer);
                compound.setString("recipe", this.recipe.name.toString());
            }
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readNBT(compound, syncing);
        this.items.deserializeNBT(compound.getCompoundTag("items"));

        if (!syncing) {
            if (compound.hasKey("recipe")) {
                this.ritualPos = BlockPos.fromLong(compound.getLong("ritual_pos"));
                this.timer = compound.getInteger("timer");
                this.recipe = TreeRitualRecipe.RECIPES.get(new ResourceLocation(compound.getString("recipe")));
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        return this.items;
    }
}
