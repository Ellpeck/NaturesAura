package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileEntityWoodStand extends TileEntityImpl implements ITickableTileEntity {

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private TreeRitualRecipe recipe;
    private BlockPos ritualPos;
    private int timer;

    public TileEntityWoodStand(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void setRitual(BlockPos pos, TreeRitualRecipe recipe) {
        this.ritualPos = pos;
        this.recipe = recipe;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.ritualPos != null && this.recipe != null) {
                if (this.world.getGameTime() % 5 == 0) {
                    if (this.isRitualOkay()) {
                        boolean wasOverHalf = this.timer >= this.recipe.time / 2;
                        this.timer += 5;
                        boolean isOverHalf = this.timer >= this.recipe.time / 2;

                        if (!isOverHalf)
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                                TileEntity tile = this.world.getTileEntity(pos);
                                if (tile instanceof TileEntityWoodStand && !((TileEntityWoodStand) tile).items.getStackInSlot(0).isEmpty()) {
                                    // TODO particles
                                  /*  PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                            (float) pos.getX() + 0.2F + this.world.rand.nextFloat() * 0.6F,
                                            (float) pos.getY() + 0.85F,
                                            (float) pos.getZ() + 0.2F + this.world.rand.nextFloat() * 0.6F,
                                            this.ritualPos.getX() + 0.5F, this.ritualPos.getY() + this.world.rand.nextFloat() * 3F + 2F, this.ritualPos.getZ() + 0.5F,
                                            this.world.rand.nextFloat() * 0.04F + 0.04F, 0x89cc37, this.world.rand.nextFloat() * 1F + 1F
                                    ));*/
                                }
                                return true;
                            });

                       /* PacketHandler.sendToAllAround(this.world, this.ritualPos, 32,
                                new PacketParticles(this.ritualPos.getX(), this.ritualPos.getY(), this.ritualPos.getZ(), 0));*/

                        if (this.timer >= this.recipe.time) {
                            this.recurseTreeDestruction(this.ritualPos, this.ritualPos);
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'G', (pos, matcher) -> {
                                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                                return true;
                            });

                            ItemEntity item = new ItemEntity(this.world,
                                    this.ritualPos.getX() + 0.5, this.ritualPos.getY() + 4.5, this.ritualPos.getZ() + 0.5,
                                    this.recipe.result.copy());
                            this.world.addEntity(item);

                            /*PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                    new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 3));*/
                            this.world.playSound(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 0.65F, 1F);

                            this.ritualPos = null;
                            this.recipe = null;
                            this.timer = 0;
                        } else if (isOverHalf && !wasOverHalf) {
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                                TileEntity tile = this.world.getTileEntity(pos);
                                if (tile instanceof TileEntityWoodStand) {
                                    TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                                    if (!stand.items.getStackInSlot(0).isEmpty()) {
                                     /*   PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                                new PacketParticles(stand.pos.getX(), stand.pos.getY(), stand.pos.getZ(), 1));*/
                                        this.world.playSound(null, stand.pos.getX() + 0.5, stand.pos.getY() + 0.5, stand.pos.getZ() + 0.5,
                                                SoundEvents.BLOCK_WOOD_STEP, SoundCategory.BLOCKS, 0.5F, 1F);

                                        stand.items.setStackInSlot(0, ItemStack.EMPTY);
                                        stand.sendToClients();
                                    }
                                }
                                return true;
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

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos offset = pos.add(x, y, z);
                    BlockState state = this.world.getBlockState(offset);
                    if (state.getBlock() instanceof LogBlock || state.getBlock() instanceof LeavesBlock) {
                        this.world.setBlockState(offset, Blocks.AIR.getDefaultState());
                        //PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), 2));

                        this.recurseTreeDestruction(offset, start);
                    }
                }
            }
        }
    }

    private boolean isRitualOkay() {
        if (!Multiblocks.TREE_RITUAL.isComplete(this.world, this.ritualPos)) {
            return false;
        }
        for (int i = 0; i < 2; i++) {
            BlockState state = this.world.getBlockState(this.ritualPos.up(i));
            if (!(state.getBlock() instanceof LogBlock))
                return false;
        }
        if (this.timer < this.recipe.time / 2) {
            List<Ingredient> required = new ArrayList<>(Arrays.asList(this.recipe.ingredients));
            boolean fine = Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                TileEntity tile = this.world.getTileEntity(pos);
                if (tile instanceof TileEntityWoodStand) {
                    ItemStack stack = ((TileEntityWoodStand) tile).items.getStackInSlot(0);
                    if (!stack.isEmpty()) {
                        for (int i = required.size() - 1; i >= 0; i--) {
                            Ingredient req = required.get(i);
                            if (req.test(stack)) {
                                required.remove(i);
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return true;
            });
            return fine && required.isEmpty();
        } else
            return true;
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            compound.put("items", this.items.serializeNBT());

        if (type == SaveType.TILE) {
            if (this.ritualPos != null && this.recipe != null) {
                compound.putLong("ritual_pos", this.ritualPos.toLong());
                compound.putInt("timer", this.timer);
                compound.putString("recipe", this.recipe.name.toString());
            }
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.items.deserializeNBT(compound.getCompound("items"));

        if (type == SaveType.TILE) {
            if (compound.contains("recipe")) {
                this.ritualPos = BlockPos.fromLong(compound.getLong("ritual_pos"));
                this.timer = compound.getInt("timer");
                this.recipe = NaturesAuraAPI.TREE_RITUAL_RECIPES.get(new ResourceLocation(compound.getString("recipe")));
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler(Direction facing) {
        return this.items;
    }
}
