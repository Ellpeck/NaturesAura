package de.ellpeck.naturesaura.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemRangeVisualizer extends ItemImpl {

    public static final ListMultimap<Integer, BlockPos> VISUALIZED_BLOCKS = ArrayListMultimap.create();
    public static final ListMultimap<Integer, Entity> VISUALIZED_ENTITIES = ArrayListMultimap.create();
    public static final ListMultimap<Integer, BlockPos> VISUALIZED_RAILS = ArrayListMultimap.create();

    public ItemRangeVisualizer() {
        super("range_visualizer");
        this.setMaxStackSize(1);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            clear();
            playerIn.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.end_all"), true);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizable) {
            if (worldIn.isRemote)
                visualize(player, VISUALIZED_BLOCKS, worldIn.provider.getDimension(), pos);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public static void clear() {
        if (!VISUALIZED_BLOCKS.isEmpty())
            VISUALIZED_BLOCKS.clear();
        if (!VISUALIZED_ENTITIES.isEmpty())
            VISUALIZED_ENTITIES.clear();
        if (!VISUALIZED_RAILS.isEmpty())
            VISUALIZED_RAILS.clear();
    }

    public static <T> void visualize(PlayerEntity player, ListMultimap<Integer, T> map, int dim, T value) {
        if (map.containsEntry(dim, value)) {
            map.remove(dim, value);
            player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.end"), true);
        } else {
            map.put(dim, value);
            player.sendStatusMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".range_visualizer.start"), true);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != this)
            return;
        Entity entity = event.getTarget();
        if (entity instanceof IVisualizable) {
            if (entity.world.isRemote) {
                int dim = entity.world.provider.getDimension();
                visualize(event.getEntityPlayer(), VISUALIZED_ENTITIES, dim, entity);
            }
            event.getEntityPlayer().swingArm(event.getHand());
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }
}
