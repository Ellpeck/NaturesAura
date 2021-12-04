package de.ellpeck.naturesaura.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemRangeVisualizer extends ItemImpl {

    public static final ListMultimap<ResourceLocation, BlockPos> VISUALIZED_BLOCKS = ArrayListMultimap.create();
    public static final ListMultimap<ResourceLocation, Entity> VISUALIZED_ENTITIES = ArrayListMultimap.create();
    public static final ListMultimap<ResourceLocation, BlockPos> VISUALIZED_RAILS = ArrayListMultimap.create();

    public ItemRangeVisualizer() {
        super("range_visualizer", new Properties().maxStackSize(1));
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static void clear() {
        if (!VISUALIZED_BLOCKS.isEmpty())
            VISUALIZED_BLOCKS.clear();
        if (!VISUALIZED_ENTITIES.isEmpty())
            VISUALIZED_ENTITIES.clear();
        if (!VISUALIZED_RAILS.isEmpty())
            VISUALIZED_RAILS.clear();
    }

    public static <T> void visualize(Player player, ListMultimap<ResourceLocation, T> map, ResourceLocation dim, T value) {
        if (map.containsEntry(dim, value)) {
            map.remove(dim, value);
            player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.end"), true);
        } else {
            map.put(dim, value);
            player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.start"), true);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            clear();
            playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.end_all"), true);
            return new ActionResult<>(InteractionResult.SUCCESS, stack);
        }
        return new ActionResult<>(InteractionResult.PASS, stack);
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizable) {
            if (level.isClientSide)
                visualize(context.getPlayer(), VISUALIZED_BLOCKS, level.func_234923_W_().func_240901_a_(), pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public class EventHandler {

        @SubscribeEvent
        public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty() || stack.getItem() != ItemRangeVisualizer.this)
                return;
            Entity entity = event.getTarget();
            if (entity instanceof IVisualizable) {
                if (entity.level.isClientSide) {
                    ResourceLocation dim = entity.level.func_234923_W_().func_240901_a_();
                    visualize(event.getPlayer(), VISUALIZED_ENTITIES, dim, entity);
                }
                event.getPlayer().swingArm(event.getHand());
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
