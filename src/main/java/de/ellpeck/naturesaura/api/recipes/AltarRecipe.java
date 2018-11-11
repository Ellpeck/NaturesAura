package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AltarRecipe {

    public final ResourceLocation name;
    public final ItemStack input;
    public final ItemStack output;
    public final Block catalyst;
    public final int aura;
    public final int time;

    public AltarRecipe(ResourceLocation name, ItemStack input, ItemStack output, Block catalyst, int aura, int time) {
        this.name = name;
        this.input = input;
        this.output = output;
        this.catalyst = catalyst;
        this.aura = aura;
        this.time = time;

        NaturesAuraAPI.ALTAR_RECIPES.put(this.name, this);
    }
}
