package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.tools.*;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

import java.util.Locale;

public final class ModItems {

    public static final Item EYE = new ItemEye();
    public static final Item GOLD_FIBER = new ItemGoldFiber();
    public static final Item GOLD_LEAF = new ItemImpl("gold_leaf");
    public static final Item INFUSED_IRON = new ItemImpl("infused_iron");
    public static final Item ANCIENT_STICK = new ItemImpl("ancient_stick");
    public static final Item COLOR_CHANGER = new ItemColorChanger();
    public static final Item AURA_CACHE = new ItemAuraCache();
    public static final Item SHOCKWAVE_CREATOR = new ItemShockwaveCreator();

    public static final Item.ToolMaterial TOOL_MATERIAL_INFUSED_IRON =
            EnumHelper.addToolMaterial(NaturesAura.MOD_ID.toUpperCase(Locale.ROOT) + "_INFUSED_IRON", 3, 300, 6.25F, 2.25F, 16);
    public static final Item INFUSED_PICKAXE = new ItemPickaxeNA("infused_iron_pickaxe", TOOL_MATERIAL_INFUSED_IRON);
    public static final Item INFUSED_AXE = new ItemAxeNA("infused_iron_axe", TOOL_MATERIAL_INFUSED_IRON, 8.25F, -3.2F);
    public static final Item INFUSED_SHOVEL = new ItemShovelNA("infused_iron_shovel", TOOL_MATERIAL_INFUSED_IRON);
    public static final Item INFUSED_HOE = new ItemHoeNA("infused_iron_hoe", TOOL_MATERIAL_INFUSED_IRON);
    public static final Item INFUSED_SWORD = new ItemSwordNA("infused_iron_sword", TOOL_MATERIAL_INFUSED_IRON);
    public static final Item MULTIBLOCK_MAKER = new ItemMultiblockMaker();
}
