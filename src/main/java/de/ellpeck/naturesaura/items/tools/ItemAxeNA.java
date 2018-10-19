package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ItemAxeNA extends ItemAxe implements IModItem, IModelProvider {
    private final String baseName;

    public ItemAxeNA(String baseName, ToolMaterial material, float damage, float speed) {
        super(material, damage, speed);
        this.baseName = baseName;
        ModRegistry.addItemOrBlock(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (this == ModItems.INFUSED_AXE && state.getMaterial() == Material.LEAVES) {
            return this.efficiency;
        } else {
            return super.getDestroySpeed(stack, state);
        }
    }
}
