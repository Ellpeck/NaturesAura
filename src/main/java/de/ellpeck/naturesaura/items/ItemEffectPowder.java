package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Vec3 hit = context.getClickLocation();
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            EntityEffectInhibitor.place(level, stack, hit.x, hit.y + 1, hit.z);
            stack.setCount(0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (ResourceLocation effect : NaturesAuraAPI.EFFECT_POWDERS.keySet()) {
                ItemStack stack = new ItemStack(this);
                setEffect(stack, effect);
                items.add(stack);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent(this.getDescriptionId(stack) + "." + getEffect(stack));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> NaturesAuraAPI.EFFECT_POWDERS.getOrDefault(getEffect(stack), 0xFFFFFF);
    }
}
