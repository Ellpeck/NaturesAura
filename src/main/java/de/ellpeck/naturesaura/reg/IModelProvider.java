package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public interface IModelProvider {

    default Map<ItemStack, ModelResourceLocation> getModelLocations() {
        ItemStack stack = this instanceof Item ? new ItemStack((Item) this) : new ItemStack((Block) this);
        String name = ((IModItem) this).getBaseName();
        return Collections.singletonMap(stack, new ModelResourceLocation(new ResourceLocation(NaturesAura.MOD_ID, name), "inventory"));
    }
}
