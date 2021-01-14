package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ItemBreakPrevention extends ItemImpl {
    public ItemBreakPrevention() {
        super("break_prevention");
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static class Events {
        @SubscribeEvent
        public void onAnvilUpdate(AnvilUpdateEvent event) {
            ItemStack stack = event.getLeft();
            if (stack.getToolTypes().isEmpty() || !stack.isDamageable())
                return;
            ItemStack second = event.getRight();
            if (second.getItem() != ModItems.BREAK_PREVENTION)
                return;
            ItemStack output = stack.copy();
            output.getOrCreateTag().putBoolean(NaturesAura.MOD_ID + ":break_prevention", true);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
        }

        @SubscribeEvent
        public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            PlayerEntity player = event.getPlayer();
            if (player == null)
                return;
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.hasTag() || !stack.getTag().getBoolean(NaturesAura.MOD_ID + ":break_prevention"))
                return;
            if (ElytraItem.isUsable(stack))
                return;
            event.setNewSpeed(0);
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (!stack.hasTag() || !stack.getTag().getBoolean(NaturesAura.MOD_ID + ":break_prevention"))
                return;
            List<ITextComponent> tooltip = event.getToolTip();
            tooltip.add(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".break_prevention").setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
            if (ElytraItem.isUsable(stack))
                return;
            if (tooltip.size() < 1)
                return;
            ITextComponent head = tooltip.get(0);
            if (head instanceof TextComponent)
                ((TextComponent) head).append(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".broken").setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
        }
    }
}
