package de.ellpeck.naturesaura.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemRangeVisualizer extends ItemImpl {

    public static final ListMultimap<ResourceLocation, BlockPos> VISUALIZED_BLOCKS = ArrayListMultimap.create();
    public static final ListMultimap<ResourceLocation, Entity> VISUALIZED_ENTITIES = ArrayListMultimap.create();
    public static final ListMultimap<ResourceLocation, BlockPos> VISUALIZED_RAILS = ArrayListMultimap.create();

    public ItemRangeVisualizer() {
        super("range_visualizer", new Properties().stacksTo(1));
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static void clear() {
        if (!ItemRangeVisualizer.VISUALIZED_BLOCKS.isEmpty())
            ItemRangeVisualizer.VISUALIZED_BLOCKS.clear();
        if (!ItemRangeVisualizer.VISUALIZED_ENTITIES.isEmpty())
            ItemRangeVisualizer.VISUALIZED_ENTITIES.clear();
        if (!ItemRangeVisualizer.VISUALIZED_RAILS.isEmpty())
            ItemRangeVisualizer.VISUALIZED_RAILS.clear();
    }

    public static <T> void visualize(Player player, ListMultimap<ResourceLocation, T> map, ResourceLocation dim, T value) {
        if (map.containsEntry(dim, value)) {
            map.remove(dim, value);
            player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".range_visualizer.end"), true);
        } else {
            map.put(dim, value);
            player.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".range_visualizer.start"), true);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (playerIn.isCrouching()) {
            ItemRangeVisualizer.clear();
            playerIn.displayClientMessage(Component.translatable("info." + NaturesAura.MOD_ID + ".range_visualizer.end_all"), true);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        var block = state.getBlock();
        if (block instanceof IVisualizable) {
            if (level.isClientSide)
                ItemRangeVisualizer.visualize(context.getPlayer(), ItemRangeVisualizer.VISUALIZED_BLOCKS, level.dimension().location(), pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public class EventHandler {

        @SubscribeEvent
        public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            var stack = event.getItemStack();
            if (stack.isEmpty() || stack.getItem() != ItemRangeVisualizer.this)
                return;
            var entity = event.getTarget();
            if (entity instanceof IVisualizable) {
                if (entity.level.isClientSide) {
                    var dim = entity.level.dimension().location();
                    ItemRangeVisualizer.visualize(event.getEntity(), ItemRangeVisualizer.VISUALIZED_ENTITIES, dim, entity);
                }
                event.getEntity().swing(event.getHand());
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
