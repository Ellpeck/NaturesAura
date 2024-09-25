package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;

public enum ModItemTier implements Tier {

    INFUSED(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6, 2, 16, () -> Ingredient.of(ModItems.INFUSED_IRON)),
    SKY(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1500, 8, 3, 12, () -> Ingredient.of(ModItems.SKY_INGOT)),
    DEPTH(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, 10, 5, 18, () -> Ingredient.of(ModItems.DEPTH_INGOT));

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairMaterial;

    ModItemTier(TagKey<Block> incorrectBlocksForDrops, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
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
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
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
