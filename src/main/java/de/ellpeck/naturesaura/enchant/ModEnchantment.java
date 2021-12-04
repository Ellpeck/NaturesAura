package de.ellpeck.naturesaura.enchant;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ModEnchantment extends Enchantment implements IModItem {

    private final String name;

    protected ModEnchantment(String name, Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        this.name = name;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.name;
    }
}
