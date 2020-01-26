package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// Name (Glowing) ambiguous?
public class ItemGlowing extends ItemImpl {
    public ItemGlowing(String baseName) {
        super(baseName, new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
