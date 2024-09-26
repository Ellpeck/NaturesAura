package de.ellpeck.naturesaura.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomCreativeTab;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class ItemEffectPowder extends ItemImpl implements IColorProvidingItem, ICustomCreativeTab {

    public ItemEffectPowder() {
        super("effect_powder");
    }

    public static ResourceLocation getEffect(ItemStack stack) {
        if (!stack.has(Data.TYPE))
            return null;
        var effect = stack.get(Data.TYPE).effect;
        if (effect.isEmpty())
            return null;
        return ResourceLocation.parse(effect);
    }

    public static ItemStack setEffect(ItemStack stack, ResourceLocation effect) {
        stack.set(Data.TYPE, new Data(effect != null ? effect.toString() : ""));
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        if (!level.isClientSide) {
            var hit = context.getClickLocation();
            var stack = context.getPlayer().getItemInHand(context.getHand());
            EntityEffectInhibitor.place(level, stack, hit.x, hit.y + 1, hit.z);
            stack.setCount(0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getCreativeTabItems() {
        return NaturesAuraAPI.EFFECT_POWDERS.keySet().stream().map(e -> ItemEffectPowder.setEffect(new ItemStack(this), e)).toList();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack) + "." + ItemEffectPowder.getEffect(stack));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> NaturesAuraAPI.EFFECT_POWDERS.getOrDefault(ItemEffectPowder.getEffect(stack), 0xFFFFFF);
    }

    public record Data(String effect) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("effect").forGetter(d -> d.effect)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
