package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.items.tools.ItemPickaxe;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

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
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(ModItems.INFUSED_IRON_PICKAXE, ModItems.SKY_PICKAXE);

        Compat.addItemTags(this);
    }

    @Override
    public TagAppender<Item> tag(Tag.Named<Item> p_126549_) {
        // super is protected, but CuriosCompat needs this
        return super.tag(p_126549_);
    }
}
