package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public interface IModelProvider {

    default Map<ItemStack, ModelVariant> getModelLocations() {
        ItemStack stack = this instanceof Item ? new ItemStack((Item) this) : new ItemStack((Block) this);
        String name = ((IModItem) this).getBaseName();
        return Collections.singletonMap(stack, new ModelVariant(new ResourceLocation(NaturesAura.MOD_ID, name), "inventory"));
    }

    class ModelVariant {

        public final ResourceLocation location;
        public final String variant;

        public ModelVariant(ResourceLocation location, String variant) {
            this.location = location;
            this.variant = variant;
        }
    }
}
