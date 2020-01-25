package de.ellpeck.naturesaura.enchant;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ModEnchantment extends Enchantment implements IModItem {

    private final String name;

    protected ModEnchantment(String name, Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
        this.name = name;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.name;
    }
}
