package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ElytraItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemBreakPrevention extends ItemImpl {

    public ItemBreakPrevention() {
        super("break_prevention");
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static class Events {

        @SubscribeEvent
        public void onAnvilUpdate(AnvilUpdateEvent event) {
            var stack = event.getLeft();
            if (!stack.isDamageableItem())
                return;
            var second = event.getRight();
            if (second.getItem() != ModItems.BREAK_PREVENTION)
                return;
            var output = stack.copy();
            output.getOrCreateTag().putBoolean(NaturesAura.MOD_ID + ":break_prevention", true);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
        }

        @SubscribeEvent
        public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            var player = event.getEntity();
            if (player == null)
                return;
            var stack = player.getMainHandItem();
            if (!stack.hasTag() || !stack.getTag().getBoolean(NaturesAura.MOD_ID + ":break_prevention"))
                return;
            if (ElytraItem.isFlyEnabled(stack))
                return;
            event.setNewSpeed(0);
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onTooltip(ItemTooltipEvent event) {
            var stack = event.getItemStack();
            if (!stack.hasTag() || !stack.getTag().getBoolean(NaturesAura.MOD_ID + ":break_prevention"))
                return;
            var tooltip = event.getToolTip();
            tooltip.add(Component.translatable("info." + NaturesAura.MOD_ID + ".break_prevention").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            if (ElytraItem.isFlyEnabled(stack))
                return;
            if (tooltip.size() < 1)
                return;
            var head = tooltip.get(0);
            if (head instanceof MutableComponent)
                ((MutableComponent) head).append(Component.translatable("info." + NaturesAura.MOD_ID + ".broken").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        }
    }
}
