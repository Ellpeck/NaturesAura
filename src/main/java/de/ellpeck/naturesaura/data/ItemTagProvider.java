package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.items.tools.*;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Comparator;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper helper) {
        super(generatorIn, blockTagProvider, NaturesAura.MOD_ID, helper);
    }

    @Override
    protected void addTags() {
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
                this.tag(Tags.Items.TOOLS_PICKAXES).add(i);
            } else if (i instanceof ItemAxe) {
                this.tag(Tags.Items.TOOLS_AXES).add(i);
            } else if (i instanceof ItemHoe) {
                this.tag(Tags.Items.TOOLS_HOES).add(i);
            } else if (i instanceof ItemSword) {
                this.tag(Tags.Items.TOOLS_SWORDS).add(i);
            } else if (i instanceof ItemShovel) {
                this.tag(Tags.Items.TOOLS_SHOVELS).add(i);
            }
        });

        Compat.addItemTags(this);
    }

    @Override
    public TagAppender<Item> tag(TagKey<Item> tag) {
        // super is protected, but CuriosCompat needs this
        return super.tag(tag);
    }
}
