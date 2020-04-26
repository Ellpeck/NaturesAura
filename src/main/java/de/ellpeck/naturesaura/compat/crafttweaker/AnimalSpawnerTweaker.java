package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@ZenRegister
@ZenCodeType.Name("mods." + NaturesAura.MOD_ID + ".AnimalSpawner")
public final class AnimalSpawnerTweaker {

    @ZenCodeType.Method
    public static void addRecipe(String name, String entity, int aura, int time, IIngredient[] ingredients) {
        ResourceLocation res = new ResourceLocation("crafttweaker", name);
        CraftTweakerAPI.apply(new AddAction<>(NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES, res, new AnimalSpawnerRecipe(res,
                ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entity)), aura, time,
                Arrays.stream(ingredients).map(IIngredient::asVanillaIngredient).toArray(Ingredient[]::new))));
    }

    @ZenCodeType.Method
    public static void removeRecipe(String name) {
        CraftTweakerAPI.apply(new RemoveAction(NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES, Collections.singletonList(new ResourceLocation(name))));
    }
}
