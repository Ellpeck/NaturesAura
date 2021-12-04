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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockEntityAnimalSpawner extends BlockEntityImpl implements ITickableBlockEntity {

    private AnimalSpawnerRecipe currentRecipe;
    private double spawnX;
    private double spawnZ;
    private int time;
    private Entity entityClient;

    public BlockEntityAnimalSpawner() {
        super(ModTileEntities.ANIMAL_SPAWNER);
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
                int drain = MathHelper.ceil(this.currentRecipe.aura / (float) this.currentRecipe.time * 10F);
                BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, drain);

                this.time += 10;
                if (this.time >= this.currentRecipe.time) {
                    Entity entity = this.currentRecipe.makeEntity(this.level, new BlockPos(this.spawnX, this.worldPosition.getY() + 1, this.spawnZ));
                    this.level.addEntity(entity);

                    this.currentRecipe = null;
                    this.time = 0;
                    this.sendToClients();
                }
            } else {
                List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.worldPosition).grow(2));

                for (AnimalSpawnerRecipe recipe : this.level.getRecipeManager().getRecipes(ModRecipes.ANIMAL_SPAWNER_TYPE, null, null)) {
                    if (recipe.ingredients.length != items.size())
                        continue;
                    List<Ingredient> required = new ArrayList<>(Arrays.asList(recipe.ingredients));
                    for (ItemEntity item : items) {
                        if (!item.isAlive() || item.cannotPickup())
                            break;
                        ItemStack stack = item.getItem();
                        if (stack.isEmpty())
                            break;
                        for (Ingredient ingredient : required) {
                            if (ingredient.test(stack) && Helper.getIngredientAmount(ingredient) == stack.getCount()) {
                                required.remove(ingredient);
                                break;
                            }
                        }
                    }
                    if (!required.isEmpty())
                        continue;

                    for (ItemEntity item : items) {
                        item.remove();
                        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                                new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.ANIMAL_SPAWNER));
                    }

                    this.currentRecipe = recipe;
                    this.spawnX = this.worldPosition.getX() + 0.5 + this.level.rand.nextFloat() * 4 - 2;
                    this.spawnZ = this.worldPosition.getZ() + 0.5 + this.level.rand.nextFloat() * 4 - 2;
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
                    this.worldPosition.getX() + (float) this.level.rand.nextGaussian() * 5F,
                    this.worldPosition.getY() + 1 + this.level.rand.nextFloat() * 5F,
                    this.worldPosition.getZ() + (float) this.level.rand.nextGaussian() * 5F,
                    this.worldPosition.getX() + this.level.rand.nextFloat(),
                    this.worldPosition.getY() + this.level.rand.nextFloat(),
                    this.worldPosition.getZ() + this.level.rand.nextFloat(),
                    this.level.rand.nextFloat() * 0.07F + 0.07F,
                    IAuraType.forLevel(this.level).getColor(),
                    this.level.rand.nextFloat() + 0.5F);

            if (this.entityClient == null) {
                this.entityClient = this.currentRecipe.makeEntity(this.level, BlockPos.ZERO);
                this.entityClient.setPosition(this.spawnX, this.worldPosition.getY() + 1, this.spawnZ);
            }
            AxisAlignedBB bounds = this.entityClient.getBoundingBox();
            for (int i = this.level.rand.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        bounds.minX + this.level.rand.nextFloat() * (bounds.maxX - bounds.minX),
                        bounds.minY + this.level.rand.nextFloat() * (bounds.maxY - bounds.minY),
                        bounds.minZ + this.level.rand.nextFloat() * (bounds.maxZ - bounds.minZ),
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
                    ResourceLocation name = new ResourceLocation(compound.getString("recipe"));
                    this.currentRecipe = (AnimalSpawnerRecipe) this.level.getRecipeManager().getRecipe(name).orElse(null);
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
