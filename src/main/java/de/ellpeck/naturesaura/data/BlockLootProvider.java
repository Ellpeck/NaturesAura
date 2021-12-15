package de.ellpeck.naturesaura.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.ellpeck.naturesaura.blocks.BlockFlowerPot;
import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.Slab;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockLootProvider implements DataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Map<Block, Function<Block, LootTable.Builder>> lootFunctions = new HashMap<>();

    public BlockLootProvider(DataGenerator generator) {
        this.generator = generator;

        for (IModItem item : ModRegistry.ALL_ITEMS) {
            if (!(item instanceof Block block))
                continue;
            if (block instanceof Slab) {
                this.lootFunctions.put(block, LootTableHooks::genSlab);
            } else if (block instanceof BlockFlowerPot) {
                this.lootFunctions.put(block, LootTableHooks::genFlowerPot);
            } else {
                this.lootFunctions.put(block, LootTableHooks::genRegular);
            }
        }

        this.lootFunctions.put(ModBlocks.ANCIENT_LEAVES, LootTableHooks::genSilkOnly);
        this.lootFunctions.put(ModBlocks.DECAYED_LEAVES, LootTableHooks::genSilkOnly);
        this.lootFunctions.put(ModBlocks.GOLDEN_LEAVES, b -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootTableHooks.survivesExplosion(b, LootItem.lootTableItem(ModItems.GOLD_LEAF)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockGoldenLeaves.STAGE, BlockGoldenLeaves.HIGHEST_STAGE)))).when(LootItemRandomChanceCondition.randomChance(0.75F))));
        this.lootFunctions.put(ModBlocks.NETHER_WART_MUSHROOM, b -> LootTableHooks.genSilkOr(b, LootItem.lootTableItem(Items.NETHER_WART).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))));
        this.lootFunctions.put(ModBlocks.NETHER_GRASS, b -> LootTableHooks.genSilkOr(b, LootItem.lootTableItem(Blocks.NETHERRACK)));
    }

    private static Path getPath(Path root, ResourceLocation res) {
        return root.resolve("data/" + res.getNamespace() + "/loot_tables/blocks/" + res.getPath() + ".json");
    }

    @Override
    public void run(HashCache cache) throws IOException {
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> function : this.lootFunctions.entrySet()) {
            Block block = function.getKey();
            Function<Block, LootTable.Builder> func = function.getValue();
            LootTable table = func.apply(block).setParamSet(LootContextParamSets.BLOCK).build();
            Path path = getPath(this.generator.getOutputFolder(), block.getRegistryName());
            DataProvider.save(GSON, cache, LootTables.serialize(table), path);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "Nature's Aura Loot";
    }

    // What a mess
    private static class LootTableHooks extends BlockLoot {

        public static LootTable.Builder genLeaves(Block block, Block drop) {
            return createLeavesDrops(block, drop, 0.05F, 0.0625F, 0.083333336F, 0.1F);
        }

        public static LootTable.Builder genSlab(Block block) {
            return createSlabItemTable(block);
        }

        public static LootTable.Builder genRegular(Block block) {
            return createSingleItemTable(block);
        }

        public static LootTable.Builder genSilkOnly(Block block) {
            return createSilkTouchOnlyTable(block);
        }

        public static LootTable.Builder genSilkOr(Block block, LootPoolEntryContainer.Builder<?> builder) {
            return createSilkTouchOrShearsDispatchTable(block, builder);
        }

        public static LootTable.Builder genFlowerPot(Block block) {
            return createPotFlowerItemTable(((FlowerPotBlock) block).getContent());
        }

        public static <T> T survivesExplosion(Block block, ConditionUserBuilder<T> then) {
            return applyExplosionCondition(block, then);
        }
    }
}