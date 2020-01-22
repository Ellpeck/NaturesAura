package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileEntityAnimalSpawner extends TileEntityImpl implements ITickableTileEntity {

    private AnimalSpawnerRecipe currentRecipe;
    private double spawnX;
    private double spawnZ;
    private int time;
    private Entity entityClient;

    public TileEntityAnimalSpawner() {
        super(ModTileEntities.ANIMAL_SPAWNER);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 10 != 0)
                return;
            if (!Multiblocks.ANIMAL_SPAWNER.isComplete(this.world, this.pos)) {
                if (this.currentRecipe != null) {
                    this.currentRecipe = null;
                    this.time = 0;
                    this.sendToClients();
                }
                return;
            }

            if (this.currentRecipe != null) {
                int drain = MathHelper.ceil(this.currentRecipe.aura / (float) this.currentRecipe.time * 10F);
                BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
                IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, drain);

                this.time += 10;
                if (this.time >= this.currentRecipe.time) {
                    Entity entity = this.currentRecipe.makeEntity(this.world, this.spawnX, this.pos.getY() + 1, this.spawnZ);
                    this.world.addEntity(entity);

                    this.currentRecipe = null;
                    this.time = 0;
                    this.sendToClients();
                }
            } else {
                List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class,
                        new AxisAlignedBB(this.pos).grow(2));

                for (AnimalSpawnerRecipe recipe : NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.values()) {
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
                        PacketHandler.sendToAllAround(this.world, this.pos, 32,
                                new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 19));
                    }

                    this.currentRecipe = recipe;
                    this.spawnX = this.pos.getX() + 0.5 + this.world.rand.nextFloat() * 4 - 2;
                    this.spawnZ = this.pos.getZ() + 0.5 + this.world.rand.nextFloat() * 4 - 2;
                    this.sendToClients();
                    break;
                }
            }
        } else {
            if (this.world.getGameTime() % 5 != 0)
                return;
            if (this.currentRecipe == null) {
                this.entityClient = null;
                return;
            }

            NaturesAuraAPI.instance().spawnParticleStream(
                    this.pos.getX() + (float) this.world.rand.nextGaussian() * 5F,
                    this.pos.getY() + 1 + this.world.rand.nextFloat() * 5F,
                    this.pos.getZ() + (float) this.world.rand.nextGaussian() * 5F,
                    this.pos.getX() + this.world.rand.nextFloat(),
                    this.pos.getY() + this.world.rand.nextFloat(),
                    this.pos.getZ() + this.world.rand.nextFloat(),
                    this.world.rand.nextFloat() * 0.07F + 0.07F,
                    IAuraType.forWorld(this.world).getColor(),
                    this.world.rand.nextFloat() + 0.5F);

            if (this.entityClient == null) {
                this.entityClient = this.currentRecipe.makeEntity(this.world, this.spawnX, this.pos.getY() + 1, this.spawnZ);
            }
            AxisAlignedBB bounds = this.entityClient.getBoundingBox();
            for (int i = this.world.rand.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        bounds.minX + this.world.rand.nextFloat() * (bounds.maxX - bounds.minX),
                        bounds.minY + this.world.rand.nextFloat() * (bounds.maxY - bounds.minY),
                        bounds.minZ + this.world.rand.nextFloat() * (bounds.maxZ - bounds.minZ),
                        0F, 0F, 0F, 0x2fd8d3, 2F, 60, 0F, false, true);
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
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
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            if (compound.contains("recipe")) {
                ResourceLocation name = new ResourceLocation(compound.getString("recipe"));
                this.currentRecipe = NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.get(name);
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
