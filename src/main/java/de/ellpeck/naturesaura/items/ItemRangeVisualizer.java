package de.ellpeck.naturesaura.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.BlockDimensionRail;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class ItemRangeVisualizer extends ItemImpl {

    public static final ListMultimap<Integer, BlockPos> VISUALIZED_BLOCKS = ArrayListMultimap.create();
    public static final ListMultimap<Integer, Entity> VISUALIZED_ENTITIES = ArrayListMultimap.create();
    public static final ListMultimap<Integer, BlockPos> VISUALIZED_RAILS = ArrayListMultimap.create();

    public ItemRangeVisualizer() {
        super("range_visualizer");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IVisualizable) {
            int dim = worldIn.provider.getDimension();
            if (worldIn.isRemote)
                if (VISUALIZED_BLOCKS.containsEntry(dim, pos))
                    VISUALIZED_BLOCKS.remove(dim, pos);
                else
                    VISUALIZED_BLOCKS.put(dim, pos);
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

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != this)
            return;
        Entity entity = event.getTarget();
        if (entity instanceof IVisualizable) {
            if (entity.world.isRemote) {
                int dim = entity.world.provider.getDimension();
                if (VISUALIZED_ENTITIES.containsEntry(dim, entity))
                    VISUALIZED_ENTITIES.remove(dim, entity);
                else
                    VISUALIZED_ENTITIES.put(dim, entity);
            }
            event.getEntityPlayer().swingArm(event.getHand());
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
