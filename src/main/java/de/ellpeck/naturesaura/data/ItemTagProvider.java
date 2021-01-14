package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(DataGenerator generatorIn, BlockTagProvider blockTagProvider, ExistingFileHelper helper) {
        super(generatorIn, blockTagProvider, NaturesAura.MOD_ID, helper);
    }

    @Override
    protected void registerTags() {
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);

        this.getOrCreateBuilder(Tags.Items.RODS_WOODEN).add(ModItems.ANCIENT_STICK);

        Compat.addItemTags(this);
    }

    @Override
    public Builder<Item> getOrCreateBuilder(ITag.INamedTag<Item> tag) {
        return super.getOrCreateBuilder(tag);
    }
}
