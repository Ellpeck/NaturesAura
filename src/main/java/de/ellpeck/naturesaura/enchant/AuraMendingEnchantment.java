package de.ellpeck.naturesaura.enchant;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class AuraMendingEnchantment extends ModEnchantment {

    public AuraMendingEnchantment() {
        super("aura_mending", Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.MENDING;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack) && stack.getCapability(NaturesAuraAPI.AURA_RECHARGE_CAPABILITY) == null;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && stack.getCapability(NaturesAuraAPI.AURA_RECHARGE_CAPABILITY) == null;
    }
}
