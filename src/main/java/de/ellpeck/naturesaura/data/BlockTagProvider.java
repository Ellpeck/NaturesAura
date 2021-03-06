package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class BlockTagProvider extends BlockTagsProvider {

    public static final Tags.IOptionalNamedTag<Block> NETHER_ALTAR_WOOD = BlockTags.createOptional(new ResourceLocation(NaturesAura.MOD_ID, "nether_altar_wood"));

    public BlockTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        this.getOrCreateBuilder(BlockTags.LOGS).add(ModBlocks.ANCIENT_LOG, ModBlocks.ANCIENT_BARK);
        this.getOrCreateBuilder(BlockTags.PLANKS).add(ModBlocks.ANCIENT_PLANKS);
        this.getOrCreateBuilder(BlockTags.STAIRS).add(ModBlocks.ANCIENT_STAIRS, ModBlocks.INFUSED_BRICK_STAIRS, ModBlocks.INFUSED_STAIRS);
        this.getOrCreateBuilder(BlockTags.LEAVES).add(ModBlocks.GOLDEN_LEAVES, ModBlocks.ANCIENT_LEAVES, ModBlocks.DECAYED_LEAVES);
        this.getOrCreateBuilder(BlockTags.RAILS).add(ModBlocks.DIMENSION_RAIL_END, ModBlocks.DIMENSION_RAIL_NETHER, ModBlocks.DIMENSION_RAIL_OVERWORLD);
        this.getOrCreateBuilder(BlockTags.SLABS).add(ModBlocks.ANCIENT_SLAB, ModBlocks.INFUSED_SLAB, ModBlocks.INFUSED_BRICK_SLAB);
        this.getOrCreateBuilder(Tags.Blocks.DIRT).add(ModBlocks.NETHER_GRASS);
        this.getOrCreateBuilder(BlockTags.SMALL_FLOWERS).add(ModBlocks.END_FLOWER, ModBlocks.AURA_BLOOM);
        this.getOrCreateBuilder(NETHER_ALTAR_WOOD).add(Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS);
    }
}
