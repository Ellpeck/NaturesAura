package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

public final class PatchouliCompat {

    public static final ResourceLocation GUI_ELEMENTS = new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/patchouli/elements.png");

    public static void initClient() {
        addPatchouliPage("altar", PageAltar.class);
        addPatchouliPage("tree_ritual", PageTreeRitual.class);

        ModelBakery.registerItemVariants(PatchouliItems.book, new ModelResourceLocation(NaturesAura.MOD_ID + ":book", "inventory"));
    }

    private static void addPatchouliPage(String name, Class<? extends BookPage> page) {
        ClientBookRegistry.INSTANCE.pageTypes.put(NaturesAura.MOD_ID + ":" + name, page);
    }
}
