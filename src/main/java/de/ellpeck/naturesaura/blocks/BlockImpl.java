package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;
import java.util.Map;

public class BlockImpl extends Block implements IModItem, IModelProvider {

    private final String baseName;

    public BlockImpl(String baseName, Material material) {
        super(material);
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
    public Map<ItemStack, ModelVariant> getModelLocations() {
        return Collections.singletonMap(new ItemStack(this), new ModelVariant(new ResourceLocation(NaturesAura.MOD_ID, this.getBaseName()), "inventory"));
    }
}
