package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public enum ModItemTier implements Tier {

    INFUSED(2, 250, 6, 2, 16, () -> Ingredient.of(ModItems.INFUSED_IRON)),
    SKY(3, 1500, 8, 3, 12, () -> Ingredient.of(ModItems.SKY_INGOT)),
    DEPTH(4, 2500, 10, 5, 18, () -> Ingredient.of(ModItems.DEPTH_INGOT));

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairMaterial;

    ModItemTier(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        this.harvestLevel = harvestLevelIn;
        this.maxUses = maxUsesIn;
        this.efficiency = efficiencyIn;
        this.attackDamage = attackDamageIn;
        this.enchantability = enchantabilityIn;
        this.repairMaterial = Lazy.of(repairMaterialIn);
    }

    @Override
    public int getUses() {
        return this.maxUses;
    }

    @Override
    public float getSpeed() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    public int getLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }
}
