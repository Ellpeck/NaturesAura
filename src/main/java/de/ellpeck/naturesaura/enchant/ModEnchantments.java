package de.ellpeck.naturesaura.enchant;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {

    public static final Enchantment AURA_MENDING = Enchantment.enchantment(
        Enchantment.definition(
            HolderSet.direct(BuiltInRegistries.ITEM.holders().filter(i -> new ItemStack(i).getCapability(NaturesAuraAPI.AURA_RECHARGE_CAPABILITY) == null).toList()),
            // same values as unbreaking, except for the max level
            5, 1, Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(55, 8), 2, EquipmentSlotGroup.ANY)
    ).build(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "aura_mending"));

}
