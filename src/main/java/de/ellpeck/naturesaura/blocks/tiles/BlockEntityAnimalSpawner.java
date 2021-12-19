package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockEntityAnimalSpawner extends BlockEntityImpl implements ITickableBlockEntity {

    private AnimalSpawnerRecipe currentRecipe;
    private double spawnX;
    private double spawnZ;
    private int time;
    private Entity entityClient;

    public BlockEntityAnimalSpawner(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANIMAL_SPAWNER, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;
            if (!Multiblocks.ANIMAL_SPAWNER.isComplete(this.level, this.worldPosition)) {
                if (this.currentRecipe != null) {
                    this.currentRecipe = null;
                    this.time = 0;
                    this.sendToClients();
                }
                return;
            }

            if (this.currentRecipe != null) {
                var drain = Mth.ceil(this.currentRecipe.aura / (float) this.currentRecipe.time * 10F);
                var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, drain);

                this.time += 10;
                if (this.time >= this.currentRecipe.time) {
                    var entity = this.currentRecipe.makeEntity(this.level, new BlockPos(this.spawnX, this.worldPosition.getY() + 1, this.spawnZ));
                    this.level.addFreshEntity(entity);

                    this.currentRecipe = null;
                    this.time = 0;
                    this.sendToClients();
                }
            } else {
                var items = this.level.getEntitiesOfClass(ItemEntity.class,
                        new AABB(this.worldPosition).inflate(2));

                for (var recipe : this.level.getRecipeManager().getRecipesFor(ModRecipes.ANIMAL_SPAWNER_TYPE, null, null)) {
                    if (recipe.ingredients.length != items.size())
                        continue;
                    List<Ingredient> required = new ArrayList<>(Arrays.asList(recipe.ingredients));
                    for (var item : items) {
                        if (!item.isAlive() || item.hasPickUpDelay())
                            break;
                        var stack = item.getItem();
                        if (stack.isEmpty())
                            break;
                        for (var ingredient : required) {
                            if (ingredient.test(stack) && Helper.getIngredientAmount(ingredient) == stack.getCount()) {
                                required.remove(ingredient);
                                break;
                            }
                        }
                    }
                    if (!required.isEmpty())
                        continue;

                    for (var item : items) {
                        item.remove(Entity.RemovalReason.KILLED);
                        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                                new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.ANIMAL_SPAWNER));
                    }

                    this.currentRecipe = recipe;
                    this.spawnX = this.worldPosition.getX() + 0.5 + this.level.random.nextFloat() * 4 - 2;
                    this.spawnZ = this.worldPosition.getZ() + 0.5 + this.level.random.nextFloat() * 4 - 2;
                    this.sendToClients();
                    break;
                }
            }
        } else {
            if (this.level.getGameTime() % 5 != 0)
                return;
            if (this.currentRecipe == null) {
                this.entityClient = null;
                return;
            }

            NaturesAuraAPI.instance().spawnParticleStream(
                    this.worldPosition.getX() + (float) this.level.random.nextGaussian() * 5F,
                    this.worldPosition.getY() + 1 + this.level.random.nextFloat() * 5F,
                    this.worldPosition.getZ() + (float) this.level.random.nextGaussian() * 5F,
                    this.worldPosition.getX() + this.level.random.nextFloat(),
                    this.worldPosition.getY() + this.level.random.nextFloat(),
                    this.worldPosition.getZ() + this.level.random.nextFloat(),
                    this.level.random.nextFloat() * 0.07F + 0.07F,
                    IAuraType.forLevel(this.level).getColor(),
                    this.level.random.nextFloat() + 0.5F);

            if (this.entityClient == null) {
                this.entityClient = this.currentRecipe.makeEntity(this.level, BlockPos.ZERO);
                this.entityClient.setPos(this.spawnX, this.worldPosition.getY() + 1, this.spawnZ);
            }
            var bounds = this.entityClient.getBoundingBox();
            for (var i = this.level.random.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        bounds.minX + this.level.random.nextFloat() * (bounds.maxX - bounds.minX),
                        bounds.minY + this.level.random.nextFloat() * (bounds.maxY - bounds.minY),
                        bounds.minZ + this.level.random.nextFloat() * (bounds.maxZ - bounds.minZ),
                        0F, 0F, 0F, 0x2fd8d3, 2F, 60, 0F, false, true);
        }
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (this.currentRecipe != null) {
                compound.putString("recipe", this.currentRecipe.name.toString());
                compound.putDouble("spawn_x", this.spawnX);
                compound.putDouble("spawn_z", this.spawnZ);
                compound.putInt("time", this.time);
            }
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (compound.contains("recipe")) {
                if (this.hasLevel()) {
                    var name = new ResourceLocation(compound.getString("recipe"));
                    this.currentRecipe = (AnimalSpawnerRecipe) this.level.getRecipeManager().byKey(name).orElse(null);
                }
                this.spawnX = compound.getDouble("spawn_x");
                this.spawnZ = compound.getDouble("spawn_z");
                this.time = compound.getInt("time");
            } else {
                this.currentRecipe = null;
                this.time = 0;
            }
        }
    }
}
