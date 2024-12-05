package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public enum ModArmorMaterial {

    INFUSED(NaturesAura.MOD_ID + ":infused_iron", 19, new int[]{2, 5, 6, 2}, 16, SoundEvents.ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.of(ModItems.INFUSED_IRON)),
    SKY(NaturesAura.MOD_ID + ":sky", 33, new int[]{3, 6, 8, 3}, 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0, () -> Ingredient.of(ModItems.SKY_INGOT)),
    DEPTH(NaturesAura.MOD_ID + ":depth", 37, new int[]{3, 6, 8, 3}, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, 3, 1, () -> Ingredient.of(ModItems.DEPTH_INGOT));

    public final Holder<ArmorMaterial> material;
    private final int maxDamageFactor;

    ModArmorMaterial(String nameIn, int maxDamageFactor, int[] damageReductionAmountsIn, int enchantabilityIn, Holder<SoundEvent> equipSoundIn, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterialSupplier) {
        this.maxDamageFactor = maxDamageFactor;
        var res = ResourceLocation.parse(nameIn);
        var defense = new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, damageReductionAmountsIn[0]);
        defense.put(ArmorItem.Type.LEGGINGS, damageReductionAmountsIn[1]);
        defense.put(ArmorItem.Type.CHESTPLATE, damageReductionAmountsIn[2]);
        defense.put(ArmorItem.Type.HELMET, damageReductionAmountsIn[3]);
        var layers = List.of(new ArmorMaterial.Layer(res));
        this.material = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, res, new ArmorMaterial(defense, enchantabilityIn, equipSoundIn, repairMaterialSupplier, layers, toughness, knockbackResistance));
    }

    public int getDurability(ArmorItem.Type type) {
        return type.getDurability(this.maxDamageFactor);
    }
}
