package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {

    public static final TagKey<Block> ALTAR_WOOD = BlockTags.create(new ResourceLocation(NaturesAura.MOD_ID, "altar_wood"));
    public static final TagKey<Block> ALTAR_STONE = BlockTags.create(new ResourceLocation(NaturesAura.MOD_ID, "altar_stone"));
    public static final TagKey<Block> ALTAR_GOLD_BRICK = BlockTags.create(new ResourceLocation(NaturesAura.MOD_ID, "altar_gold_brick"));
    public static final TagKey<Block> ALTAR_FANCY_BRICK = BlockTags.create(new ResourceLocation(NaturesAura.MOD_ID, "altar_fancy_brick"));

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

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ANIMAL_CONTAINER, ModBlocks.ANIMAL_GENERATOR,
                ModBlocks.ANIMAL_SPAWNER, ModBlocks.AURA_DETECTOR, ModBlocks.AURA_TIMER, ModBlocks.BLAST_FURNACE_BOOSTER,
                ModBlocks.CHORUS_GENERATOR, ModBlocks.CHUNK_LOADER, ModBlocks.CONVERSION_CATALYST, ModBlocks.CRUSHING_CATALYST,
                ModBlocks.DEPTH_INGOT_BLOCK, ModBlocks.ENDER_CRATE, ModBlocks.FIELD_CREATOR, ModBlocks.FIREWORK_GENERATOR,
                ModBlocks.FURNACE_HEATER, ModBlocks.GENERATOR_LIMIT_REMOVER, ModBlocks.GOLD_BRICK, ModBlocks.GOLD_NETHER_BRICK,
                ModBlocks.GRATED_CHUTE, ModBlocks.HOPPER_UPGRADE, ModBlocks.INFUSED_BRICK, ModBlocks.INFUSED_BRICK_SLAB,
                ModBlocks.INFUSED_BRICK_STAIRS, ModBlocks.INFUSED_IRON_BLOCK, ModBlocks.INFUSED_SLAB, ModBlocks.INFUSED_STAIRS,
                ModBlocks.INFUSED_STONE, ModBlocks.ITEM_DISTRIBUTOR, ModBlocks.LOWER_LIMITER, ModBlocks.MOSS_GENERATOR,
                ModBlocks.NATURE_ALTAR, ModBlocks.NETHER_GRASS, ModBlocks.PICKUP_STOPPER, ModBlocks.PLACER, ModBlocks.POTION_GENERATOR,
                ModBlocks.POWDER_PLACER, ModBlocks.PROJECTILE_GENERATOR, ModBlocks.RF_CONVERTER, ModBlocks.SKY_INGOT_BLOCK,
                ModBlocks.SNOW_CREATOR, ModBlocks.SPAWN_LAMP, ModBlocks.SPRING, ModBlocks.TAINTED_GOLD_BLOCK,
                ModBlocks.TIME_CHANGER, ModBlocks.WEATHER_CHANGER);

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.ANCIENT_BARK, ModBlocks.ANCIENT_LOG,
                ModBlocks.ANCIENT_PLANKS, ModBlocks.ANCIENT_SLAB, ModBlocks.ANCIENT_STAIRS,
                ModBlocks.AUTO_CRAFTER, ModBlocks.FLOWER_GENERATOR, ModBlocks.NETHER_WART_MUSHROOM,
                ModBlocks.OAK_GENERATOR, ModBlocks.OFFERING_TABLE, ModBlocks.WOOD_STAND);
    }
}
