package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.tools.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public final class ModItems {

    public static final ToolMaterial TOOL_INFUSED = EnumHelper.addToolMaterial(
            NaturesAura.MOD_ID_UPPER + "_INFUSED_IRON", 3, 300, 6.25F, 2.25F, 16);
    public static final Item INFUSED_PICKAXE = new ItemPickaxeNA("infused_iron_pickaxe", TOOL_INFUSED);
    public static final Item INFUSED_AXE = new ItemAxeNA("infused_iron_axe", TOOL_INFUSED, 8.25F, -3.2F);
    public static final Item INFUSED_SHOVEL = new ItemShovelNA("infused_iron_shovel", TOOL_INFUSED);
    public static final Item INFUSED_HOE = new ItemHoeNA("infused_iron_hoe", TOOL_INFUSED);
    public static final Item INFUSED_SWORD = new ItemSwordNA("infused_iron_sword", TOOL_INFUSED);
    public static final ArmorMaterial ARMOR_INFUSED = EnumHelper.addArmorMaterial(
            NaturesAura.MOD_ID_UPPER + "INFUSED_IRON", NaturesAura.MOD_ID + ":infused_iron",
            19, new int[]{2, 5, 6, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0F);
    public static final Item INFUSED_HELMET = new ItemArmorNA("infused_iron_helmet", ARMOR_INFUSED, EntityEquipmentSlot.HEAD);
    public static final Item INFUSED_CHEST = new ItemArmorNA("infused_iron_chest", ARMOR_INFUSED, EntityEquipmentSlot.CHEST);
    public static final Item INFUSED_PANTS = new ItemArmorNA("infused_iron_pants", ARMOR_INFUSED, EntityEquipmentSlot.LEGS);
    public static final Item INFUSED_SHOES = new ItemArmorNA("infused_iron_shoes", ARMOR_INFUSED, EntityEquipmentSlot.FEET);

    public static final Item EYE = new ItemEye("eye");
    public static final Item EYE_IMPROVED = new ItemEye("eye_improved");
    public static final Item GOLD_FIBER = new ItemGoldFiber();
    public static final Item GOLD_LEAF = new ItemImpl("gold_leaf");
    public static final Item INFUSED_IRON = new ItemImpl("infused_iron");
    public static final Item ANCIENT_STICK = new ItemImpl("ancient_stick");
    public static final Item COLOR_CHANGER = new ItemColorChanger();
    public static final Item AURA_CACHE = new ItemAuraCache();
    public static final Item SHOCKWAVE_CREATOR = new ItemShockwaveCreator();
    public static final Item MULTIBLOCK_MAKER = new ItemMultiblockMaker();
    public static final Item BOTTLE_TWO = new ItemImpl("bottle_two_the_rebottling");
    public static final Item AURA_BOTTLE = new ItemAuraBottle();
    public static final Item FARMING_STENCIL = new ItemImpl("farming_stencil");
    public static final Item SKY_INGOT = new ItemImpl("sky_ingot");
    public static final Item CALLING_SPIRIT = new ItemGlowing("calling_spirit");
    public static final Item EFFECT_POWDER = new ItemEffectPowder();
    public static final Item BIRTH_SPIRIT = new ItemBirthSpirit();
    public static final Item MOVER_MINECART = new ItemMoverMinecart();
}
