package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class EntityEffectInhibitor extends Entity implements IVisualizable {

    private static final EntityDataAccessor<String> INHIBITED_EFFECT = SynchedEntityData.defineId(EntityEffectInhibitor.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityEffectInhibitor.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AMOUNT = SynchedEntityData.defineId(EntityEffectInhibitor.class, EntityDataSerializers.INT);
    private ResourceLocation lastEffect;
    private boolean powderListDirty;

    @OnlyIn(Dist.CLIENT)
    public int renderTicks;

    public EntityEffectInhibitor(EntityType<?> entityTypeIn, Level levelIn) {
        super(entityTypeIn, levelIn);
    }

    public static void place(Level level, ItemStack stack, double posX, double posY, double posZ) {
        var effect = ItemEffectPowder.getEffect(stack);
        var entity = new EntityEffectInhibitor(ModEntities.EFFECT_INHIBITOR, level);
        entity.setInhibitedEffect(effect);
        entity.setColor(NaturesAuraAPI.EFFECT_POWDERS.get(effect));
        entity.setAmount(stack.getCount());
        entity.setPos(posX, posY, posZ);
        level.addFreshEntity(entity);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.powderListDirty = true;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        // we pass a null effect because we want to remove our effect from the world
        this.updatePowderListStatus(null);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EntityEffectInhibitor.INHIBITED_EFFECT, null);
        this.entityData.define(EntityEffectInhibitor.COLOR, 0);
        this.entityData.define(EntityEffectInhibitor.AMOUNT, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (EntityEffectInhibitor.INHIBITED_EFFECT.equals(key) || EntityEffectInhibitor.AMOUNT.equals(key))
            this.powderListDirty = true;
    }

    @Override
    public void setPos(double x, double y, double z) {
        if (x != this.getX() || y != this.getY() || z != this.getZ())
            this.powderListDirty = true;
        super.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.powderListDirty)
            this.updatePowderListStatus(this.getInhibitedEffect());

        if (this.level.isClientSide) {
            if (this.level.getGameTime() % 5 == 0) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        this.getX() + this.level.random.nextGaussian() * 0.1F,
                        this.getY(),
                        this.getZ() + this.level.random.nextGaussian() * 0.1F,
                        this.level.random.nextGaussian() * 0.005F,
                        this.level.random.nextFloat() * 0.03F,
                        this.level.random.nextGaussian() * 0.005F,
                        this.getColor(), this.level.random.nextFloat() * 3F + 1F, 120, 0F, true, true);
            }
            this.renderTicks++;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setInhibitedEffect(new ResourceLocation(compound.getString("effect")));
        this.setColor(compound.getInt("color"));
        this.setAmount(compound.contains("amount") ? compound.getInt("amount") : 24);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("effect", this.getInhibitedEffect().toString());
        compound.putInt("color", this.getColor());
        compound.putInt("amount", this.getAmount());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof Player player && (!this.level.mayInteract(player, this.blockPosition()) || this.hurt(DamageSource.playerAttack(player), 0.0F));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && !this.level.isClientSide) {
            this.spawnAtLocation(this.getDrop(), 0F);
            this.kill();
            return true;
        } else {
            return super.hurt(source, amount);
        }
    }

    @Override
    public boolean isPickable() {
        // SOMEHOW, this actually means "is raytraceable" IN GENERAL even though getPickResult is about middle-clicking, jesus fucking christ mojang
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getDrop();
    }

    public ItemStack getDrop() {
        return ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, this.getAmount()), this.getInhibitedEffect());
    }

    public ResourceLocation getInhibitedEffect() {
        var effect = this.entityData.get(EntityEffectInhibitor.INHIBITED_EFFECT);
        if (effect == null || effect.isEmpty())
            return null;
        return new ResourceLocation(effect);
    }

    public void setInhibitedEffect(ResourceLocation effect) {
        this.entityData.set(EntityEffectInhibitor.INHIBITED_EFFECT, effect != null ? effect.toString() : null);
    }

    public int getColor() {
        return this.entityData.get(EntityEffectInhibitor.COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(EntityEffectInhibitor.COLOR, color);
    }

    public int getAmount() {
        return this.entityData.get(EntityEffectInhibitor.AMOUNT);
    }

    public void setAmount(int amount) {
        this.entityData.set(EntityEffectInhibitor.AMOUNT, amount);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        return Helper.aabb(this.getEyePosition()).inflate(this.getAmount());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return this.getColor();
    }

    private void updatePowderListStatus(ResourceLocation inhibitedEffect) {
        var powders = ((LevelData) ILevelData.getLevelData(this.level)).effectPowders;
        if (this.lastEffect != null) {
            var oldList = powders.get(this.lastEffect);
            oldList.removeIf(t -> this.getEyePosition().equals(t.getA()));
        }
        if (inhibitedEffect != null) {
            var newList = powders.get(inhibitedEffect);
            newList.add(new Tuple<>(this.getEyePosition(), this.getAmount()));
        }
        this.powderListDirty = false;
        this.lastEffect = inhibitedEffect;
    }
}
