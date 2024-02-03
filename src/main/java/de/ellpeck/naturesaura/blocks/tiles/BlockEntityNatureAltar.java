package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.BasicAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlockEntityNatureAltar extends BlockEntityImpl implements ITickableBlockEntity {

    private final BasicAuraContainer container = new BasicAuraContainer(null, 500000) {
        @Override
        public int getAuraColor() {
            return IAuraType.forLevel(BlockEntityNatureAltar.this.level).getColor();
        }
    };
    private final ItemStack[] catalysts = new ItemStack[4];
    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return BlockEntityNatureAltar.this.getRecipeForInput(stack) != null || stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).isPresent();
        }

        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            var cap = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).orElse(null);
            if (cap != null) {
                return cap.storeAura(1, true) <= 0;
            } else {
                return BlockEntityNatureAltar.this.getRecipeForInput(stack) == null;
            }
        }
    };
    @OnlyIn(Dist.CLIENT)
    public int bobTimer;
    public boolean isComplete;

    private AltarRecipe currentRecipe;
    private int timer;
    private int lastAura;
    private boolean firstTick = true;

    public BlockEntityNatureAltar(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NATURE_ALTAR, pos, state);
    }

    @Override
    public void tick() {
        var rand = this.level.random;

        if (this.level.getGameTime() % 40 == 0) {
            var index = 0;
            for (var x = -2; x <= 2; x += 4) {
                for (var z = -2; z <= 2; z += 4) {
                    var offset = this.worldPosition.offset(x, 1, z);
                    var state = this.level.getBlockState(offset);
                    this.catalysts[index] = new ItemStack(state.getBlock());
                    index++;
                }
            }
        }

        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 40 == 0 || this.firstTick) {
                var complete = Multiblocks.ALTAR.isComplete(this.level, this.worldPosition);
                if (complete != this.isComplete) {
                    this.isComplete = complete;
                    this.sendToClients();
                }
                this.firstTick = false;
            }

            if (this.isComplete) {
                var type = IAuraType.forLevel(this.level);
                var space = this.container.storeAura(300, true);
                if (space > 0 && (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER))) {
                    var toStore = Math.min(IAuraChunk.getAuraInArea(this.level, this.worldPosition, 20), space);
                    if (toStore > 0) {
                        var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 20, this.worldPosition);
                        var chunk = IAuraChunk.getAuraChunk(this.level, spot);

                        chunk.drainAura(spot, toStore);
                        this.container.storeAura(toStore, false);

                        if (this.level.getGameTime() % 3 == 0)
                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                                    this.worldPosition.getX() + (float) rand.nextGaussian() * 10F,
                                    this.worldPosition.getY() + rand.nextFloat() * 10F,
                                    this.worldPosition.getZ() + (float) rand.nextGaussian() * 10F,
                                    this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F,
                                    rand.nextFloat() * 0.1F + 0.1F, this.container.getAuraColor(), rand.nextFloat() + 1F
                            ));
                    }
                }

                var stack = this.items.getStackInSlot(0);
                var container = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER, null).orElse(null);
                if (!stack.isEmpty() && container != null) {
                    var theoreticalDrain = this.container.drainAura(1000, true);
                    if (theoreticalDrain > 0) {
                        var stored = container.storeAura(theoreticalDrain, false);
                        if (stored > 0) {
                            this.container.drainAura(stored, false);

                            if (this.level.getGameTime() % 4 == 0)
                                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.ALTAR_CONVERSION, this.container.getAuraColor()));
                        }
                    }
                } else {
                    if (this.currentRecipe == null) {
                        if (!stack.isEmpty()) {
                            this.currentRecipe = this.getRecipeForInput(stack);
                        }
                    } else {
                        if (stack.isEmpty() || !this.currentRecipe.input.test(stack)) {
                            this.currentRecipe = null;
                            this.timer = 0;
                        } else {
                            var req = Mth.ceil(this.currentRecipe.aura / (double) this.currentRecipe.time);
                            if (this.container.getStoredAura() >= req) {
                                this.container.drainAura(req, false);

                                if (this.timer % 4 == 0)
                                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.ALTAR_CONVERSION, this.container.getAuraColor()));

                                this.timer++;
                                if (this.timer >= this.currentRecipe.time) {
                                    this.items.setStackInSlot(0, this.currentRecipe.output.copy());
                                    this.currentRecipe = null;
                                    this.timer = 0;

                                    this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, SoundEvents.ARROW_HIT_PLAYER, SoundSource.BLOCKS, 0.65F, 1F);
                                }
                            }
                        }
                    }
                }
            }

            if (this.level.getGameTime() % 10 == 0 && this.lastAura != this.container.getStoredAura()) {
                this.lastAura = this.container.getStoredAura();
                this.sendToClients();
            }
        } else {
            if (this.isComplete) {
                if (rand.nextFloat() >= 0.7F) {
                    var fourths = this.container.getMaxAura() / 4;
                    if (this.container.getStoredAura() > 0) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() - 4F + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + 4F + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 2) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() - 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 3) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }

                }
            }

            this.bobTimer++;
        }
    }

    private AltarRecipe getRecipeForInput(ItemStack input) {
        for (var recipe : this.level.getRecipeManager().getRecipesFor(ModRecipes.ALTAR_TYPE, null, this.level)) {
            if (recipe.input.test(input)) {
                if (recipe.catalyst == Ingredient.EMPTY)
                    return recipe;
                for (var stack : this.catalysts) {
                    if (recipe.catalyst.test(stack))
                        return recipe;
                }
            }
        }
        return null;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.items.serializeNBT());
            compound.putBoolean("complete", this.isComplete);
            this.container.writeNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (this.currentRecipe != null) {
                compound.putString("recipe", this.currentRecipe.name.toString());
                compound.putInt("timer", this.timer);
            }
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompound("items"));
            this.isComplete = compound.getBoolean("complete");
            this.container.readNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (compound.contains("recipe")) {
                if (this.hasLevel())
                    this.currentRecipe = (AltarRecipe) this.level.getRecipeManager().byKey(new ResourceLocation(compound.getString("recipe"))).orElse(null);
                this.timer = compound.getInt("timer");
            }
        }
    }

    @Override
    public IAuraContainer getAuraContainer() {
        return this.container;
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.items;
    }
}
