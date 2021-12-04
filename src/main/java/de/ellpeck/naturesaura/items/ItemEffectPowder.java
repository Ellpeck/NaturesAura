package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEffectPowder extends ItemImpl implements IColorProvidingItem {

    public ItemEffectPowder() {
        super("effect_powder");
    }

    public static ResourceLocation getEffect(ItemStack stack) {
        if (!stack.hasTag())
            return null;
        String effect = stack.getTag().getString("effect");
        if (effect.isEmpty())
            return null;
        return new ResourceLocation(effect);
    }

    public static ItemStack setEffect(ItemStack stack, ResourceLocation effect) {
        stack.getOrCreateTag().putString("effect", effect != null ? effect.toString() : "");
        return stack;
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Vector3d hit = context.getHitVec();
            ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
            EntityEffectInhibitor.place(level, stack, hit.x, hit.y + 1, hit.z);
            stack.setCount(0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            for (ResourceLocation effect : NaturesAuraAPI.EFFECT_POWDERS.keySet()) {
                ItemStack stack = new ItemStack(this);
                setEffect(stack, effect);
                items.add(stack);
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(this.getTranslationKey(stack) + "." + getEffect(stack));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> NaturesAuraAPI.EFFECT_POWDERS.getOrDefault(getEffect(stack), 0xFFFFFF);
    }
}
