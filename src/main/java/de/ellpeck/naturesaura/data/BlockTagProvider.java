package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IAxeBreakable;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {

    public static final TagKey<Block> ALTAR_WOOD = BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "altar_wood"));
    public static final TagKey<Block> ALTAR_STONE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "altar_stone"));
    public static final TagKey<Block> ALTAR_GOLD_BRICK = BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "altar_gold_brick"));
    public static final TagKey<Block> ALTAR_FANCY_BRICK = BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "altar_fancy_brick"));

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NaturesAura.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.LOGS).add(ModBlocks.ANCIENT_LOG, ModBlocks.ANCIENT_BARK);
        this.tag(BlockTags.PLANKS).add(ModBlocks.ANCIENT_PLANKS);
        this.tag(BlockTags.STAIRS).add(ModBlocks.ANCIENT_STAIRS, ModBlocks.INFUSED_BRICK_STAIRS, ModBlocks.INFUSED_STAIRS);
        this.tag(BlockTags.LEAVES).add(ModBlocks.GOLDEN_LEAVES, ModBlocks.ANCIENT_LEAVES, ModBlocks.DECAYED_LEAVES);
        this.tag(BlockTags.RAILS).add(ModBlocks.DIMENSION_RAIL_END, ModBlocks.DIMENSION_RAIL_NETHER, ModBlocks.DIMENSION_RAIL_OVERWORLD);
        this.tag(BlockTags.SLABS).add(ModBlocks.ANCIENT_SLAB, ModBlocks.INFUSED_SLAB, ModBlocks.INFUSED_BRICK_SLAB);
        this.tag(BlockTags.DIRT).add(ModBlocks.NETHER_GRASS);
        this.tag(BlockTags.SMALL_FLOWERS).add(ModBlocks.END_FLOWER, ModBlocks.AURA_BLOOM);
        this.tag(BlockTagProvider.ALTAR_WOOD).addTag(BlockTags.PLANKS).add(Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS);
        this.tag(BlockTagProvider.ALTAR_STONE).addTag(BlockTags.STONE_BRICKS).add(Blocks.NETHER_BRICKS);
        this.tag(BlockTagProvider.ALTAR_GOLD_BRICK).add(ModBlocks.GOLD_BRICK, ModBlocks.GOLD_NETHER_BRICK);
        this.tag(BlockTagProvider.ALTAR_FANCY_BRICK).add(Blocks.RED_NETHER_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.DEPTH_INGOT_BLOCK, ModBlocks.SKY_INGOT_BLOCK, ModBlocks.INFUSED_IRON_BLOCK);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModData.getAllModItems().filter(i -> i instanceof IPickaxeBreakable).map(i -> (Block) i).toArray(Block[]::new));
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CONVERSION_CATALYST, ModBlocks.CRUSHING_CATALYST,
            ModBlocks.DEPTH_INGOT_BLOCK, ModBlocks.GOLD_BRICK, ModBlocks.GOLD_NETHER_BRICK, ModBlocks.INFUSED_STONE, ModBlocks.INFUSED_BRICK,
            ModBlocks.INFUSED_BRICK_SLAB, ModBlocks.INFUSED_BRICK_STAIRS, ModBlocks.INFUSED_IRON_BLOCK, ModBlocks.INFUSED_SLAB,
            ModBlocks.INFUSED_STAIRS, ModBlocks.SKY_INGOT_BLOCK, ModBlocks.TAINTED_GOLD_BLOCK);

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModData.getAllModItems().filter(i -> i instanceof IAxeBreakable).map(i -> (Block) i).toArray(Block[]::new));
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.ANCIENT_PLANKS, ModBlocks.ANCIENT_SLAB, ModBlocks.ANCIENT_STAIRS, ModBlocks.NETHER_WART_MUSHROOM);
    }

}
