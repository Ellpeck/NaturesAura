package de.ellpeck.naturesaura.reg;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IModelProvider {

    Map<ItemStack, ModelVariant> getModelLocations();

    class ModelVariant {

        public final ResourceLocation location;
        public final String variant;

        public ModelVariant(ResourceLocation location, String variant) {
            this.location = location;
            this.variant = variant;
        }
    }
}
