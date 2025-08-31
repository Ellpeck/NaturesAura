package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.blocks.BlockFlowerPot;
import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.Slab;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class BlockLootProvider extends BlockLootSubProvider {

    public BlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        ModData.getAllModItems().forEach(item -> {
            if (!(item instanceof Block block))
                return;
            if (block instanceof Slab) {
                this.add(block, this::createSlabItemTable);
            } else if (block instanceof BlockFlowerPot) {
                this.add(block, this::createPotFlowerItemTable);
            } else {
                this.dropSelf(block);
            }
        });

        this.add(ModBlocks.ANCIENT_LEAVES, this::createSilkTouchOnlyTable);
        this.add(ModBlocks.DECAYED_LEAVES, this::createSilkTouchOnlyTable);
        this.add(ModBlocks.GOLDEN_LEAVES, b -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(this.applyExplosionCondition(b, LootItem.lootTableItem(ModItems.GOLD_LEAF)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockGoldenLeaves.STAGE, BlockGoldenLeaves.HIGHEST_STAGE)))).when(LootItemRandomChanceCondition.randomChance(0.75F))));
        this.add(ModBlocks.NETHER_WART_MUSHROOM, b -> this.createSilkTouchDispatchTable(b, LootItem.lootTableItem(Items.NETHER_WART).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))));
        this.add(ModBlocks.NETHER_GRASS, b -> this.createSilkTouchDispatchTable(b, LootItem.lootTableItem(Blocks.NETHERRACK)));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModData.getAllModItems().filter(i -> i instanceof Block).map(i -> (Block) i).toList();
    }

}
