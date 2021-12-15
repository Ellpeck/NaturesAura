package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.WeatherType;
import de.ellpeck.naturesaura.api.misc.WeightedOre;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public final class ModRecipes {

    public static final RecipeType<AltarRecipe> ALTAR_TYPE = new RecipeType<>();
    public static final RecipeSerializer<AltarRecipe> ALTAR_SERIAIZER = new AltarRecipe.Serializer();

    public static final RecipeType<AnimalSpawnerRecipe> ANIMAL_SPAWNER_TYPE = new RecipeType<>();
    public static final RecipeSerializer<AnimalSpawnerRecipe> ANIMAL_SPAWNER_SERIALIZER = new AnimalSpawnerRecipe.Serializer();

    public static final RecipeType<OfferingRecipe> OFFERING_TYPE = new RecipeType<>();
    public static final RecipeSerializer<OfferingRecipe> OFFERING_SERIALIZER = new OfferingRecipe.Serializer();

    public static final RecipeType<TreeRitualRecipe> TREE_RITUAL_TYPE = new RecipeType<>();
    public static final RecipeSerializer<TreeRitualRecipe> TREE_RITUAL_SERIALIZER = new TreeRitualRecipe.Serializer();

    public static void register(IForgeRegistry<RecipeSerializer<?>> registry) {
        register(registry, "altar", ALTAR_TYPE, ALTAR_SERIAIZER);
        register(registry, "animal_spawner", ANIMAL_SPAWNER_TYPE, ANIMAL_SPAWNER_SERIALIZER);
        register(registry, "offering", OFFERING_TYPE, OFFERING_SERIALIZER);
        register(registry, "tree_ritual", TREE_RITUAL_TYPE, TREE_RITUAL_SERIALIZER);
    }

    public static void init() {
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE.defaultBlockState(),
                Blocks.MOSSY_COBBLESTONE.defaultBlockState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.STONE_BRICKS.defaultBlockState(),
                Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE_WALL.defaultBlockState(),
                Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.STONE_BRICK_WALL.defaultBlockState(),
                Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());

        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/coal", 5000);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/coal", 5000);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/iron", 3000);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/iron", 3000);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/gold", 500);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/gold", 500);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/diamond", 50);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/diamond", 50);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/lapis", 250);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/lapis", 250);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/redstone", 200);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/redstone", 200);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/emerald", 30);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/emerald", 30);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/quartz", 8000);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/netherite_scrap", 30);

        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/copper", 2000);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/copper", 2000);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/tin", 1800);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/tin", 1800);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/lead", 1500);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/lead", 1500);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/silver", 1000);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/silver", 1000);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/nickel", 100);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/nickel", 100);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/platinum", 20);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/nether/platinum", 20);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/aluminum", 1200);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/aluminium", 1200);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/osmium", 1500);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/zinc", 1000);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/yellorite", 1200);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/uranium", 400);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/apatite", 700);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/ruby", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/peridot", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/topaz", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/tanzanite", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/malachite", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/sapphire", 40);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/amber", 150);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/resonating", 50);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/sulfur", 600);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/saltpeter", 250);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/firestone", 30);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/salt", 2900);
        ore(NaturesAuraAPI.OVERWORLD_ORES, "ores/draconium", 5);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/cobalt", 50);
        ore(NaturesAuraAPI.NETHER_ORES, "ores/ardite", 50);

        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.EGG, 10000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.SNOWBALL, 7000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.SMALL_FIREBALL, 35000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.ENDER_PEARL, 50000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.EXPERIENCE_BOTTLE, 200000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.ARROW, 30000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.SPECTRAL_ARROW, 40000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.SHULKER_BULLET, 300000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.LLAMA_SPIT, 100000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.TRIDENT, 3000000);

        NaturesAuraAPI.WEATHER_CHANGER_CONVERSIONS.put(new ItemStack(Blocks.SUNFLOWER), WeatherType.SUN);
        NaturesAuraAPI.WEATHER_CHANGER_CONVERSIONS.put(new ItemStack(Items.DARK_PRISMARINE), WeatherType.RAIN);
        NaturesAuraAPI.WEATHER_CHANGER_CONVERSIONS.put(new ItemStack(Items.FIRE_CHARGE), WeatherType.THUNDERSTORM);
    }

    private static void ore(List<WeightedOre> list, String name, int weight) {
        var res = new ResourceLocation("forge", name);
        list.add(new WeightedOre(res, weight));
    }

    private static void register(IForgeRegistry<RecipeSerializer<?>> registry, String name, RecipeType<?> type, RecipeSerializer<?> serializer) {
        var res = new ResourceLocation(NaturesAura.MOD_ID, name);
        Registry.register(Registry.RECIPE_TYPE, res, type);
        registry.register(serializer.setRegistryName(res));
    }

    public static JsonObject serializeStack(ItemStack stack) {
        var nbt = stack.save(new CompoundTag());
        var c = nbt.getByte("Count");
        if (c != 1) {
            nbt.putByte("count", c);
        }
        nbt.remove("Count");
        renameTag(nbt, "id", "item");
        renameTag(nbt, "tag", "nbt");
        var dyn = new Dynamic<Tag>(NbtOps.INSTANCE, nbt);
        return dyn.convert(JsonOps.INSTANCE).getValue().getAsJsonObject();
    }

    private static void renameTag(CompoundTag nbt, String oldName, String newName) {
        var tag = nbt.get(oldName);
        if (tag != null) {
            nbt.remove(oldName);
            nbt.put(newName, tag);
        }
    }

    private static class RecipeType<T extends Recipe<?>> implements net.minecraft.world.item.crafting.RecipeType<T> {

        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
