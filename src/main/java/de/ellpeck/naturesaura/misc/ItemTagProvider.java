package de.ellpeck.naturesaura.misc;

import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);

        this.getBuilder(Tags.Items.RODS_WOODEN).add(ModItems.ANCIENT_STICK);
    }
}
