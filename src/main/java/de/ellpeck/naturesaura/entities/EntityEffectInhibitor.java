package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ItemInhibitingPowder;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityEffectInhibitor extends Entity {

    private static final DataParameter<String> INHIBITED_EFFECT = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.STRING);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.VARINT);

    public EntityEffectInhibitor(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {
        this.setSize(0.25F, 0.25F);
        this.dataManager.register(INHIBITED_EFFECT, null);
        this.dataManager.register(COLOR, 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setInhibitedEffect(new ResourceLocation(compound.getString("effect")));
        this.setColor(compound.getInteger("color"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("effect", this.getInhibitedEffect().toString());
        compound.setInteger("color", this.getColor());
    }

    @Override
    public void onEntityUpdate() {
        if (this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 5 == 0) {
                NaturesAura.proxy.spawnMagicParticle(
                        this.posX + this.world.rand.nextGaussian() * 0.1F,
                        this.posY,
                        this.posZ + this.world.rand.nextGaussian() * 0.1F,
                        this.world.rand.nextGaussian() * 0.005F,
                        this.world.rand.nextFloat() * 0.03F,
                        this.world.rand.nextGaussian() * 0.005F,
                        this.getColor(), this.world.rand.nextFloat() * 3F + 1F, 120, 0F, true, true);
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && !this.world.isRemote) {
            this.setDead();
            this.entityDropItem(ItemInhibitingPowder.setEffect(new ItemStack(ModItems.INHIBITING_POWDER), this.getInhibitedEffect()), 0F);
            return true;
        } else
            return super.attackEntityFrom(source, amount);
    }

    public void setInhibitedEffect(ResourceLocation effect) {
        this.dataManager.set(INHIBITED_EFFECT, effect.toString());
    }

    public ResourceLocation getInhibitedEffect() {
        return new ResourceLocation(this.dataManager.get(INHIBITED_EFFECT));
    }

    public void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    public int getColor() {
        return this.dataManager.get(COLOR);
    }
}
