package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EffectPowder extends ItemImpl implements IColorProvidingItem {

    public EffectPowder() {
        super("effect_powder", new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            BlockPos pos = context.getPos();
            Vec3d hit = context.getHitVec();
            ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
            EntityEffectInhibitor.place(world, stack, pos.getX() + hit.x, pos.getY() + hit.y + 1, pos.getZ() + hit.z);
            stack.setCount(0);
        }
        return ActionResultType.SUCCESS;
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
        return new TranslationTextComponent(this.getTranslationKey(stack) + "." + getEffect(stack) + ".name");
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
        stack.getOrCreateTag().putString("effect", effect.toString());
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> NaturesAuraAPI.EFFECT_POWDERS.getOrDefault(getEffect(stack), 0xFFFFFF);
    }
}
