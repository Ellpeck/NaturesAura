package de.ellpeck.naturesaura.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            clear();
            playerIn.sendStatusMessage(new TextComponentTranslation("info." + NaturesAura.MOD_ID + ".range_visualizer.end_all"), true);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizable) {
            if (worldIn.isRemote)
                visualize(player, VISUALIZED_BLOCKS, worldIn.provider.getDimension(), pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public static void clear() {
        if (!VISUALIZED_BLOCKS.isEmpty())
            VISUALIZED_BLOCKS.clear();
        if (!VISUALIZED_ENTITIES.isEmpty())
            VISUALIZED_ENTITIES.clear();
        if (!VISUALIZED_RAILS.isEmpty())
            VISUALIZED_RAILS.clear();
    }

    public static <T> void visualize(EntityPlayer player, ListMultimap<Integer, T> map, int dim, T value) {
        if (map.containsEntry(dim, value)) {
            map.remove(dim, value);
            player.sendStatusMessage(new TextComponentTranslation("info." + NaturesAura.MOD_ID + ".range_visualizer.end"), true);
        } else {
            map.put(dim, value);
            player.sendStatusMessage(new TextComponentTranslation("info." + NaturesAura.MOD_ID + ".range_visualizer.start"), true);
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
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
