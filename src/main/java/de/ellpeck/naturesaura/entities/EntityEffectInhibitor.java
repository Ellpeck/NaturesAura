package de.ellpeck.naturesaura.entities;

import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class EntityEffectInhibitor extends Entity implements IVisualizable {

    private static final DataParameter<String> INHIBITED_EFFECT = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.STRING);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> AMOUNT = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.VARINT);
    private ResourceLocation lastEffect;
    private boolean powderListDirty;

    @OnlyIn(Dist.CLIENT)
    public int renderTicks;

    public EntityEffectInhibitor(EntityType<?> entityTypeIn, Level levelIn) {
        super(entityTypeIn, levelIn);
    }

    public static void place(Level level, ItemStack stack, double posX, double posY, double posZ) {
        ResourceLocation effect = ItemEffectPowder.getEffect(stack);
        EntityEffectInhibitor entity = new EntityEffectInhibitor(ModEntities.EFFECT_INHIBITOR, level);
        entity.setInhibitedEffect(effect);
        entity.setColor(NaturesAuraAPI.EFFECT_POWDERS.get(effect));
        entity.setAmount(stack.getCount());
        entity.setPosition(posX, posY, posZ);
        level.addEntity(entity);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.powderListDirty = true;
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        this.setInhibitedEffect(null);
        this.updatePowderListStatus();
    }

    @Override
    protected void registerData() {
        this.dataManager.register(INHIBITED_EFFECT, null);
        this.dataManager.register(COLOR, 0);
        this.dataManager.register(AMOUNT, 0);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (INHIBITED_EFFECT.equals(key) || AMOUNT.equals(key))
            this.powderListDirty = true;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (x != this.getPosX() || y != this.getPosY() || z != this.getPosZ())
            this.powderListDirty = true;
        super.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.powderListDirty)
            this.updatePowderListStatus();

        if (this.level.isClientSide) {
            if (this.level.getGameTime() % 5 == 0) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.getPosX() + this.level.rand.nextGaussian() * 0.1F,
                        this.getPosY(),
                        this.getPosZ() + this.level.rand.nextGaussian() * 0.1F,
                        this.level.rand.nextGaussian() * 0.005F,
                        this.level.rand.nextFloat() * 0.03F,
                        this.level.rand.nextGaussian() * 0.005F,
                        this.getColor(), this.level.rand.nextFloat() * 3F + 1F, 120, 0F, true, true);
            }
            this.renderTicks++;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void readAdditional(CompoundTag compound) {
        this.setInhibitedEffect(new ResourceLocation(compound.getString("effect")));
        this.setColor(compound.getInt("color"));
        this.setAmount(compound.contains("amount") ? compound.getInt("amount") : 24);
    }

    @Override
    protected void writeAdditional(CompoundTag compound) {
        compound.putString("effect", this.getInhibitedEffect().toString());
        compound.putInt("color", this.getColor());
        compound.putInt("amount", this.getAmount());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && !this.level.isClientSide) {
            this.remove();
            this.entityDropItem(this.getDrop(), 0F);
            return true;
        } else
            return super.attackEntityFrom(source, amount);
    }

    public ItemStack getDrop() {
        return ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, this.getAmount()), this.getInhibitedEffect());
    }

    public ResourceLocation getInhibitedEffect() {
        String effect = this.dataManager.get(INHIBITED_EFFECT);
        if (effect == null || effect.isEmpty())
            return null;
        return new ResourceLocation(effect);
    }

    public void setInhibitedEffect(ResourceLocation effect) {
        this.dataManager.set(INHIBITED_EFFECT, effect != null ? effect.toString() : null);
    }

    public int getColor() {
        return this.dataManager.get(COLOR);
    }

    public void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    public int getAmount() {
        return this.dataManager.get(AMOUNT);
    }

    public void setAmount(int amount) {
        this.dataManager.set(AMOUNT, amount);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(Level level, BlockPos pos) {
        return Helper.aabb(this.getPositionVec()).grow(this.getAmount());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return this.getColor();
    }

    private void updatePowderListStatus() {
        ListMultimap<ResourceLocation, Tuple<Vector3d, Integer>> powders = ((LevelData) ILevelData.getLevelData(this.level)).effectPowders;
        if (this.lastEffect != null) {
            List<Tuple<Vector3d, Integer>> oldList = powders.get(this.lastEffect);
            oldList.removeIf(t -> this.getPositionVec().equals(t.getA()));
        }
        ResourceLocation effect = this.getInhibitedEffect();
        if (effect != null) {
            List<Tuple<Vector3d, Integer>> newList = powders.get(effect);
            newList.add(new Tuple<>(this.getPositionVec(), this.getAmount()));
        }
        this.powderListDirty = false;
        this.lastEffect = effect;
    }
}
