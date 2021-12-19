package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockEntityWoodStand extends BlockEntityImpl implements ITickableBlockEntity {

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private TreeRitualRecipe recipe;
    private BlockPos ritualPos;
    private int timer;

    public BlockEntityWoodStand(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WOOD_STAND, pos, state);
    }

    public void setRitual(BlockPos pos, TreeRitualRecipe recipe) {
        this.ritualPos = pos;
        this.recipe = recipe;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.ritualPos != null && this.recipe != null) {
                if (this.level.getGameTime() % 5 == 0) {
                    if (this.isRitualOkay()) {
                        var wasOverHalf = this.timer >= this.recipe.time / 2;
                        this.timer += 5;
                        var isOverHalf = this.timer >= this.recipe.time / 2;

                        if (!isOverHalf)
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                                var tile = this.level.getBlockEntity(pos);
                                if (tile instanceof BlockEntityWoodStand && !((BlockEntityWoodStand) tile).items.getStackInSlot(0).isEmpty()) {
                                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                                            (float) pos.getX() + 0.2F + this.level.random.nextFloat() * 0.6F,
                                            (float) pos.getY() + 0.85F,
                                            (float) pos.getZ() + 0.2F + this.level.random.nextFloat() * 0.6F,
                                            this.ritualPos.getX() + 0.5F, this.ritualPos.getY() + this.level.random.nextFloat() * 3F + 2F, this.ritualPos.getZ() + 0.5F,
                                            this.level.random.nextFloat() * 0.04F + 0.04F, 0x89cc37, this.level.random.nextFloat() + 1F
                                    ));
                                }
                                return true;
                            });

                        PacketHandler.sendToAllAround(this.level, this.ritualPos, 32,
                                new PacketParticles(this.ritualPos.getX(), this.ritualPos.getY(), this.ritualPos.getZ(), PacketParticles.Type.TR_GOLD_POWDER));

                        if (this.timer >= this.recipe.time) {
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'G', (pos, matcher) -> {
                                this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                return true;
                            });
                            recurseTreeDestruction(this.level, this.ritualPos, this.ritualPos, true, false);

                            var item = new ItemEntity(this.level,
                                    this.ritualPos.getX() + 0.5, this.ritualPos.getY() + 4.5, this.ritualPos.getZ() + 0.5,
                                    this.recipe.result.copy());
                            this.level.addFreshEntity(item);

                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                                    new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.TR_SPAWN_RESULT));
                            this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5,
                                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.65F, 1F);

                            this.ritualPos = null;
                            this.recipe = null;
                            this.timer = 0;
                        } else if (isOverHalf && !wasOverHalf) {
                            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                                var tile = this.level.getBlockEntity(pos);
                                if (tile instanceof BlockEntityWoodStand stand && !stand.items.getStackInSlot(0).isEmpty()) {
                                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                                            new PacketParticles(stand.worldPosition.getX(), stand.worldPosition.getY(), stand.worldPosition.getZ(), PacketParticles.Type.TR_CONSUME_ITEM));
                                    this.level.playSound(null, stand.worldPosition.getX() + 0.5, stand.worldPosition.getY() + 0.5, stand.worldPosition.getZ() + 0.5,
                                            SoundEvents.WOOD_STEP, SoundSource.BLOCKS, 0.5F, 1F);

                                    stand.items.setStackInSlot(0, ItemStack.EMPTY);
                                    stand.sendToClients();
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

    public static void recurseTreeDestruction(Level level, BlockPos pos, BlockPos start, boolean includeLeaves, boolean drop) {
        if (Math.abs(pos.getX() - start.getX()) >= 6
                || Math.abs(pos.getZ() - start.getZ()) >= 6
                || Math.abs(pos.getY() - start.getY()) >= 32) {
            return;
        }

        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                for (var z = -1; z <= 1; z++) {
                    var offset = pos.offset(x, y, z);
                    var state = level.getBlockState(offset);
                    if (state.getBlock().getTags().contains(BlockTags.LOGS.getName()) || includeLeaves && state.getBlock() instanceof LeavesBlock) {
                        if (drop) {
                            level.destroyBlock(offset, true);
                        } else {
                            // in this case we don't want the particles, so we can't use destroyBlock
                            level.setBlockAndUpdate(offset, Blocks.AIR.defaultBlockState());
                            PacketHandler.sendToAllAround(level, pos, 32, new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), PacketParticles.Type.TR_DISAPPEAR));
                        }
                        recurseTreeDestruction(level, offset, start, includeLeaves, drop);
                    }
                }
            }
        }
    }

    private boolean isRitualOkay() {
        if (!Multiblocks.TREE_RITUAL.isComplete(this.level, this.ritualPos)) {
            return false;
        }
        for (var i = 0; i < 2; i++) {
            var state = this.level.getBlockState(this.ritualPos.above(i));
            if (!(state.getBlock().getTags().contains(BlockTags.LOGS.getName())))
                return false;
        }
        if (this.timer < this.recipe.time / 2) {
            List<Ingredient> required = new ArrayList<>(Arrays.asList(this.recipe.ingredients));
            var fine = Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                var tile = this.level.getBlockEntity(pos);
                if (tile instanceof BlockEntityWoodStand) {
                    var stack = ((BlockEntityWoodStand) tile).items.getStackInSlot(0);
                    if (!stack.isEmpty()) {
                        for (var i = required.size() - 1; i >= 0; i--) {
                            var req = required.get(i);
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
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            compound.put("items", this.items.serializeNBT());

        if (type == SaveType.TILE) {
            if (this.ritualPos != null && this.recipe != null) {
                compound.putLong("ritual_pos", this.ritualPos.asLong());
                compound.putInt("timer", this.timer);
                compound.putString("recipe", this.recipe.name.toString());
            }
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.items.deserializeNBT(compound.getCompound("items"));

        if (type == SaveType.TILE) {
            if (compound.contains("recipe")) {
                this.ritualPos = BlockPos.of(compound.getLong("ritual_pos"));
                this.timer = compound.getInt("timer");
                if (this.hasLevel())
                    this.recipe = (TreeRitualRecipe) this.level.getRecipeManager().byKey(new ResourceLocation(compound.getString("recipe"))).orElse(null);
            }
        }
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.items;
    }
}
