package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.items.tools.*;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, NaturesAura.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);

        this.tag(Tags.Items.RODS_WOODEN).add(ModItems.ANCIENT_STICK);

        // sort these so that they don't change the json every time we run data (because it's a set)
        ModRegistry.ALL_ITEMS.stream().sorted(Comparator.comparing(IModItem::getBaseName)).filter(i -> i instanceof Item).map(i -> (Item) i).forEach(i -> {
            if (i instanceof ItemPickaxe) {
                this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(i);
                this.tag(ItemTags.PICKAXES).add(i);
            } else if (i instanceof ItemAxe) {
                this.tag(ItemTags.AXES).add(i);
            } else if (i instanceof ItemHoe) {
                this.tag(ItemTags.HOES).add(i);
            } else if (i instanceof ItemSword) {
                this.tag(ItemTags.SWORDS).add(i);
            } else if (i instanceof ItemShovel) {
                this.tag(ItemTags.SHOVELS).add(i);
            } else if (i instanceof ItemArmor a) {
                var tag = switch (a.getType()) {
                    case HELMET -> ItemTags.HEAD_ARMOR;
                    case CHESTPLATE -> ItemTags.CHEST_ARMOR;
                    case LEGGINGS -> ItemTags.LEG_ARMOR;
                    case BOOTS -> ItemTags.FOOT_ARMOR;
                    default -> null;
                };
                if (tag != null)
                    this.tag(tag).add(i);
            }
        });

        Compat.addItemTags(this);
    }

    @Override
    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
        // super is protected, but CuriosCompat needs this
        return super.tag(tag);
    }

}
