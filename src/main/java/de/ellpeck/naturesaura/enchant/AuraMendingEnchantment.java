package de.ellpeck.naturesaura.enchant;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class AuraMendingEnchantment extends ModEnchantment {

    public AuraMendingEnchantment() {
        super("aura_mending", Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotType.values());
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && ench != Enchantments.MENDING;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return super.canApply(stack) && !stack.getCapability(NaturesAuraAPI.capAuraRecharge).isPresent();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && !stack.getCapability(NaturesAuraAPI.capAuraRecharge).isPresent();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
