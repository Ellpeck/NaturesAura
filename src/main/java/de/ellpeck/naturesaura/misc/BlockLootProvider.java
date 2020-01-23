package de.ellpeck.naturesaura.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.ellpeck.naturesaura.blocks.Slab;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.SetCount;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockLootProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Map<Block, Function<Block, LootTable.Builder>> lootFunctions = new HashMap<>();

    public BlockLootProvider(DataGenerator generator) {
        this.generator = generator;

        for (IModItem item : ModRegistry.ALL_ITEMS) {
            if (!(item instanceof Block))
                continue;
            Block block = (Block) item;
            if (block instanceof Slab) {
                this.lootFunctions.put(block, BlockLootProvider::genSlab);
            } else {
                this.lootFunctions.put(block, BlockLootProvider::genRegular);
            }
        }
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> function : this.lootFunctions.entrySet()) {
            Block block = function.getKey();
            Function<Block, LootTable.Builder> func = function.getValue();
            LootTable table = func.apply(block).setParameterSet(LootParameterSets.BLOCK).build();
            Path path = getPath(this.generator.getOutputFolder(), block.getRegistryName());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(table), path);
        }
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    private static LootTable.Builder genSlab(Block b) {
        LootEntry.Builder<?> entry = ItemLootEntry.builder(b)
                .acceptFunction(SetCount.func_215932_a(ConstantRange.of(2)).acceptCondition(BlockStateProperty.builder(b).with(SlabBlock.TYPE, SlabType.DOUBLE)))
                .acceptFunction(ExplosionDecay.func_215863_b());
        return LootTable.builder().addLootPool(LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry));
    }

    private static LootTable.Builder genRegular(Block b) {
        LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
        LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry)
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(pool);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Nature's Aura Loot";
    }
}