package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class Sword extends SwordItem implements IModItem, IModelProvider {
    private final String baseName;

    public Sword(String baseName, IItemTier material, int damage, float speed) {
        super(material, damage, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this == ModItems.INFUSED_SWORD)
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 2));
        return super.hitEntity(stack, target, attacker);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (this == ModItems.INFUSED_SWORD)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }
}
